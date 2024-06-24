package com.enegoce.service;

import com.enegoce.entities.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class MTService {

    @Autowired
    private MtFieldMappingService mappingService;

    @Autowired
    private DealService dealService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    private static final Logger logger = LogManager.getLogger(DealService.class);

    ////////////////////MT Generation////////////////////
    /////////////////////////////////////////////////////

    public boolean generateAndExportMtMessage(Long dealId, String mt, String filePath, boolean generateXml) {
        List<MtFieldMapping> mappings = mappingService.mappingsByMt(mt);
        if (mappings.isEmpty()) {
            logger.error("No mappings found for mt: " + mt);
            return false;
        }

        mappings.sort(Comparator.comparingInt(MtFieldMapping::getFieldOrder));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            InfoDeal infoDeal = dealService.dealById(dealId);
            List<DealGoods> dealGoodsList = dealService.goodsByDealId(dealId);
            List<DealParty> dealPartiesList = dealService.partiesByDealId(dealId);
            List<Settlement> settlementList = dealService.settlementsByDealId(dealId);
            List<DealComment> dealCommentsList = dealService.commentsByDealId(dealId);

            if (generateXml) {
                return generateAndExportMtMessageWithXml(infoDeal, dealGoodsList, dealPartiesList, settlementList, dealCommentsList, writer, mappings, mt, true);
            } else {
                return generateAndExportMtMessageWithWriter(infoDeal, dealGoodsList, dealPartiesList, settlementList, dealCommentsList, writer, mappings);
            }
        } catch (Exception e) {
            logger.error("Error generating MT message: " + e);
            return false;
        }
    }

    private boolean generateAndExportMtMessageWithWriter(InfoDeal infoDeal, List<DealGoods> dealGoodsList, List<DealParty> dealPartiesList, List<Settlement> settlementList, List<DealComment> dealCommentsList, BufferedWriter writer, List<MtFieldMapping> mappings) {
        try {
            processInfoDeal(writer, infoDeal, dealGoodsList, dealPartiesList, settlementList, dealCommentsList, mappings);
            return true;
        } catch (Exception e) {
            logger.error("Error processing data: " + e);
            return false;
        }
    }

    private boolean generateAndExportMtMessageWithXml(InfoDeal infoDeal, List<DealGoods> dealGoodsList, List<DealParty> dealPartiesList, List<Settlement> settlementList, List<DealComment> dealCommentsList, BufferedWriter writer, List<MtFieldMapping> mappings, String mt, boolean includeXmlHeader) {
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(writer);

            if (includeXmlHeader) {
                xmlWriter.writeStartDocument("UTF-8", "1.0");
                xmlWriter.writeCharacters("\n"); // Ensure new line after XML declaration
            }

            xmlWriter.writeStartElement("MT" + mt);
            xmlWriter.writeCharacters("\n"); // Ensure new line after <MT>

            processInfoDealForXml(xmlWriter, infoDeal, dealGoodsList, dealPartiesList, settlementList, dealCommentsList, mappings);

            xmlWriter.writeEndElement(); // End MT700 or MT701
            xmlWriter.writeCharacters("\n"); // Ensure new line after </MT>
            if (includeXmlHeader) {
                xmlWriter.writeEndDocument();
            }
            xmlWriter.close();

            return true;
        } catch (Exception e) {
            logger.error("Error generating XML MT message: " + e);
            return false;
        }
    }

    private void processInfoDealForXml(XMLStreamWriter xmlWriter, InfoDeal infoDeal, List<DealGoods> dealGoodsList, List<DealParty> dealPartiesList, List<Settlement> settlementList, List<DealComment> dealCommentsList, List<MtFieldMapping> mappings) throws XMLStreamException {
        for (MtFieldMapping mapping : mappings) {
            String entityName = mapping.getEntityName();
            String fieldName = mapping.getDatabaseField();
            String mtTag = mapping.getTag();
            String mappingRule = mapping.getMappingRule();

            if (mappingRule != null && !mappingRule.isEmpty()) {
                processMappingRuleForXml(xmlWriter, mappingRule, mtTag, infoDeal, dealPartiesList, settlementList, dealCommentsList);
            } else {
                if (fieldName == null || entityName == null || fieldName.contains("//todo//") || entityName.contains("//todo//")) {
                    continue; //Temporary skip
                }

                String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                Object fieldValue = null;

                try {
                    if ("DealGoods".equals(entityName)) {
                        fieldValue = getDealGoodsFieldValue(fieldName, getterMethodName, dealGoodsList);
                    } else {
                        fieldValue = getFieldValue(entityName, getterMethodName, infoDeal, dealPartiesList, settlementList, dealCommentsList, null, null);
                    }
                } catch (Exception e) {
                    logger.error("Error accessing field " + fieldName + " in entity " + entityName, e);
                }

                if (fieldValue != null) {
                    xmlWriter.writeCharacters("\t"); // Add indentation for <field> element
                    xmlWriter.writeStartElement("field");
                    xmlWriter.writeCharacters("\n\t\t"); // Add indentation for <tag> element
                    xmlWriter.writeStartElement("tag");
                    xmlWriter.writeCharacters(mtTag);
                    xmlWriter.writeEndElement(); // End tag
                    xmlWriter.writeCharacters("\n\t\t"); // Add indentation for <value> element
                    xmlWriter.writeStartElement("value");
                    xmlWriter.writeCharacters(formatFieldValue(fieldValue));
                    xmlWriter.writeEndElement(); // End value
                    xmlWriter.writeCharacters("\n\t"); // Add indentation before closing </field>
                    xmlWriter.writeEndElement(); // End field
                    xmlWriter.writeCharacters("\n"); // Ensure new line after </field>
                }
            }
        }
    }

    private void processMappingRuleForXml(XMLStreamWriter xmlWriter, String mappingRule, String mtTag, InfoDeal infoDeal, List<DealParty> dealPartiesList, List<Settlement> settlementList, List<DealComment> dealCommentsList) {
        try {
            JSONObject ruleJson = new JSONObject(mappingRule);
            JSONArray fieldsArray = ruleJson.optJSONArray("fields");
            String delimiter = ruleJson.optString("delimiter", "");
            String code = ruleJson.optString("code", null);

            if (fieldsArray != null) {
                StringBuilder combinedValue = new StringBuilder();
                DealParty party = code != null ? dealService.partyByDealIdAndCode(infoDeal.getId(), code) : null;
                DealComment comment = code != null ? dealService.commentByDealAndType(infoDeal.getId(), code) : null;

                if (party == null && code != null) {
                    logger.warn("No DealParty found for code: " + code);
                }
                if (comment == null && code != null) {
                    logger.warn("No DealComment found for code: " + code);
                }

                for (int i = 0; i < fieldsArray.length(); i++) {
                    String[] parts = fieldsArray.getString(i).split("\\.");
                    String entity = parts[0];
                    String field = parts[1];
                    String getterMethodName = "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);

                    Object fieldValue = getFieldValue(entity, getterMethodName, infoDeal, dealPartiesList, settlementList, dealCommentsList, party, comment);

                    if (fieldValue != null) {
                        if (!combinedValue.isEmpty()) {
                            combinedValue.append(delimiter);
                        }
                        combinedValue.append(formatFieldValue(fieldValue));
                    }
                }

                if (!combinedValue.isEmpty()) {
                    xmlWriter.writeCharacters("\t"); // Add indentation for <field> element
                    xmlWriter.writeStartElement("field");
                    xmlWriter.writeCharacters("\n\t\t"); // Add indentation for <tag> element
                    xmlWriter.writeStartElement("tag");
                    xmlWriter.writeCharacters(mtTag);
                    xmlWriter.writeEndElement(); // End tag
                    xmlWriter.writeCharacters("\n\t\t"); // Add indentation for <value> element
                    xmlWriter.writeStartElement("value");
                    xmlWriter.writeCharacters(combinedValue.toString());
                    xmlWriter.writeEndElement(); // End value
                    xmlWriter.writeCharacters("\n\t"); // Add indentation before closing </field>
                    xmlWriter.writeEndElement(); // End field
                    xmlWriter.writeCharacters("\n"); // Ensure new line after </field>
                }
            }
        } catch (JSONException e) {
            logger.error("Error parsing mappingRule: " + mappingRule, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void processInfoDeal(BufferedWriter writer, InfoDeal infoDeal, List<DealGoods> dealGoodsList, List<DealParty> dealPartiesList, List<Settlement> settlementList, List<DealComment> dealCommentsList, List<MtFieldMapping> mappings) throws IOException {
        for (MtFieldMapping mapping : mappings) {
            String entityName = mapping.getEntityName();
            String fieldName = mapping.getDatabaseField();
            String mtTag = mapping.getTag();
            String mappingRule = mapping.getMappingRule();

            if (mappingRule != null && !mappingRule.isEmpty()) {
                processMappingRule(writer, mappingRule, mtTag, infoDeal, dealPartiesList, settlementList, dealCommentsList);
            } else {
                if (fieldName == null || entityName == null || fieldName.contains("//todo//") || entityName.contains("//todo//")) {
                    continue;
                }

                String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                Object fieldValue = null;

                try {
                    if ("DealGoods".equals(entityName)) {
                        fieldValue = getDealGoodsFieldValue(fieldName, getterMethodName, dealGoodsList);
                    } else {
                        fieldValue = getFieldValue(entityName, getterMethodName, infoDeal, dealPartiesList, settlementList, dealCommentsList, null, null);
                    }
                } catch (Exception e) {
                    logger.error("Error accessing field " + fieldName + " in entity " + entityName, e);
                }

                if (fieldValue != null) {
                    writer.write(mtTag + ":" + formatFieldValue(fieldValue) + "\r\n");
                }
            }
        }
    }

    private void processMappingRule(BufferedWriter writer, String mappingRule, String mtTag, InfoDeal infoDeal, List<DealParty> dealPartiesList, List<Settlement> settlementList, List<DealComment> dealCommentsList) {
        try {
            JSONObject ruleJson = new JSONObject(mappingRule);
            JSONArray fieldsArray = ruleJson.optJSONArray("fields");
            String delimiter = ruleJson.optString("delimiter", "");
            String code = ruleJson.optString("code", null);

            if (fieldsArray != null) {
                StringBuilder combinedValue = new StringBuilder();
                DealParty party = code != null ? dealService.partyByDealIdAndCode(infoDeal.getId(), code) : null;
                DealComment comment = code != null ? dealService.commentByDealAndType(infoDeal.getId(), code) : null;

                if (party == null && code != null) {
                    logger.warn("No DealParty found for code: " + code);
                }
                if (comment == null && code != null) {
                    logger.warn("No DealComment found for code: " + code);
                }

                for (int i = 0; i < fieldsArray.length(); i++) {
                    String[] parts = fieldsArray.getString(i).split("\\.");
                    String entity = parts[0];
                    String field = parts[1];
                    String getterMethodName = "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);

                    Object fieldValue = getFieldValue(entity, getterMethodName, infoDeal, dealPartiesList, settlementList, dealCommentsList, party, comment);

                    if (fieldValue != null) {
                        if (!combinedValue.isEmpty()) {
                            combinedValue.append(delimiter);
                        }
                        combinedValue.append(formatFieldValue(fieldValue));
                    }
                }

                if (!combinedValue.isEmpty()) {
                    writer.write(mtTag + ":" + combinedValue + "\r\n");
                }
            }
        } catch (JSONException e) {
            logger.error("Error parsing mappingRule: " + mappingRule, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getFieldValue(String entityName, String getterMethodName, InfoDeal infoDeal, List<DealParty> dealPartiesList, List<Settlement> settlementList, List<DealComment> dealCommentsList, DealParty party, DealComment comment) throws Exception {
        return switch (entityName) {
            case "InfoDeal" -> infoDeal.getClass().getMethod(getterMethodName).invoke(infoDeal);
            case "Settlement" -> getFieldValueFromList(settlementList, getterMethodName);
            case "DealParty" ->
                    party != null ? party.getClass().getMethod(getterMethodName).invoke(party) : getFieldValueFromList(dealPartiesList, getterMethodName);
            case "DealComment" ->
                    comment != null ? comment.getClass().getMethod(getterMethodName).invoke(comment) : getFieldValueFromList(dealCommentsList, getterMethodName);
            default -> null;
        };
    }

    private Object getFieldValueFromList(List<?> list, String getterMethodName) {
        for (Object obj : list) {
            try {
                Object value = obj.getClass().getMethod(getterMethodName).invoke(obj);
                if (value != null) {
                    return value;
                }
            } catch (Exception e) {
                logger.error("Error accessing field using method " + getterMethodName + " in list element", e);
            }
        }
        return null;
    }

    private Object getDealGoodsFieldValue(String fieldName, String getterMethodName, List<DealGoods> dealGoodsList) throws Exception {

        StringBuilder goodsValues = new StringBuilder();

        if ("goodsDesc".equals(fieldName)) {
            for (DealGoods dealGoods : dealGoodsList) {
                Object goodsValue = dealGoods.getClass().getMethod(getterMethodName).invoke(dealGoods);
                if (goodsValue != null) {
                    if (!goodsValues.isEmpty()) {
                        goodsValues.append("\n+");
                    } else {
                        goodsValues.append("+");
                    }
                    goodsValues.append(formatFieldValue(goodsValue));
                }
            }
        } else {
            for (DealGoods dealGoods : dealGoodsList) {
                Object goodsValue = dealGoods.getClass().getMethod(getterMethodName).invoke(dealGoods);
                if (goodsValue != null) {
                    if (!goodsValues.isEmpty()) {
                        goodsValues.append(", ");  // Adjustable delimiter
                    }
                    goodsValues.append(formatFieldValue(goodsValue));
                }
            }
        }
        return !goodsValues.isEmpty() ? goodsValues.toString() : null;
    }

    private String formatFieldValue(Object fieldValue) {
        if (fieldValue instanceof Date date) {
            return new java.text.SimpleDateFormat("yyMMdd").format(date);
        } else if (fieldValue instanceof java.time.LocalDate) {
            return ((java.time.LocalDate) fieldValue).format(DATE_FORMATTER);
        } else {
            return fieldValue.toString();
        }
    }

    public boolean generateAndExportMt798Message(Long dealId, String mt, String filePath, String format) {
        if (!"700".equals(mt) && !"701".equals(mt)) {
            logger.error("Unsupported MT for MT798: " + mt);
            return false;
        }

        List<MtFieldMapping> mappings = mappingService.mappingsByMt(mt);
        if (mappings.isEmpty()) {
            logger.error("No mappings found for mt: " + mt);
            return false;
        }

        mappings.sort(Comparator.comparingInt(MtFieldMapping::getFieldOrder));
        logger.info(mappings);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            InfoDeal infoDeal = dealService.dealById(dealId);
            List<DealGoods> dealGoodsList = dealService.goodsByDealId(dealId);
            List<DealParty> dealPartiesList = dealService.partiesByDealId(dealId);
            List<Settlement> settlementList = dealService.settlementsByDealId(dealId);
            List<DealComment> dealCommentsList = dealService.commentsByDealId(dealId);

            if ("txt".equalsIgnoreCase(format)) {
                writer.write("12:" + mt + "\r\n");
                writer.write("=========================\r\n");
                return generateAndExportMtMessageWithWriter(infoDeal, dealGoodsList, dealPartiesList, settlementList, dealCommentsList, writer, mappings);
            } else if ("xml".equalsIgnoreCase(format)) {
                return generateAndExportMt798MessageWithXml(infoDeal, dealGoodsList, dealPartiesList, settlementList, dealCommentsList, writer, mappings, mt);
            } else {
                logger.error("Unsupported output format: " + format);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error generating MT798: " + e);
            return false;
        }
    }

    private boolean generateAndExportMt798MessageWithXml(InfoDeal infoDeal, List<DealGoods> dealGoodsList, List<DealParty> dealPartiesList, List<Settlement> settlementList, List<DealComment> dealCommentsList, BufferedWriter writer, List<MtFieldMapping> mappings, String mt) {
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(writer);

            xmlWriter.writeStartDocument("UTF-8", "1.0");
            xmlWriter.writeCharacters("\n");
            xmlWriter.writeStartElement("MT798");
            xmlWriter.writeCharacters("\n");
            xmlWriter.writeStartElement("header");
            xmlWriter.writeCharacters("\n\t"); // Add indentation for <field>
            xmlWriter.writeStartElement("field");
            xmlWriter.writeCharacters("\n\t\t"); // Add indentation for <tag>
            xmlWriter.writeStartElement("tag");
            xmlWriter.writeCharacters("12");
            xmlWriter.writeEndElement(); // End tag
            xmlWriter.writeCharacters("\n\t\t"); // Add indentation for <value>
            xmlWriter.writeStartElement("value");
            xmlWriter.writeCharacters(mt); // set the value based on the mt parameter
            xmlWriter.writeEndElement(); // End value
            xmlWriter.writeCharacters("\n\t"); // Indentation before closing </field>
            xmlWriter.writeEndElement(); // End field
            xmlWriter.writeCharacters("\n"); // Indentation before closing </header>
            xmlWriter.writeEndElement(); // End header
            xmlWriter.writeCharacters("\n");
            xmlWriter.writeStartElement("body");
            xmlWriter.writeCharacters("\n");

            // Call existing XML generation logic
            generateAndExportMtMessageWithXml(infoDeal, dealGoodsList, dealPartiesList, settlementList, dealCommentsList, writer, mappings, mt, false);

            xmlWriter.writeEndElement(); // End body
            xmlWriter.writeCharacters("\n");
            xmlWriter.writeEndElement(); // End MT798
            xmlWriter.writeCharacters("\n");
            xmlWriter.writeEndDocument();
            xmlWriter.close();

            return true;
        } catch (Exception e) {
            logger.error("Error generating XML MT798 message: " + e);
            return false;
        }
    }


    /////////////////////TXT/XMLto Database////////////////
    //////////////////////////////////////////////////////


    //TODO: XML/TXT to database

    //////////////////////TXT to XML///////////////////////
    ///////////////////////////////////////////////////////

    public File convertTextToXml(File textFile, String outputFilePath) throws IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;

        try {
            builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
        } catch (Exception e) {
            throw new IOException("Error initializing XML document", e);
        }

        Element rootElement = doc.createElement("MT700");
        doc.appendChild(rootElement);

        try (BufferedReader br = new BufferedReader(new FileReader(textFile))) {
            String line;
            StringBuilder valueBuilder = new StringBuilder();
            String currentTag = null;
            while ((line = br.readLine()) != null) {
                if (line.contains(":") && !line.startsWith(" ")) {
                    if (currentTag != null) {
                        addField(doc, rootElement, currentTag, valueBuilder.toString());
                    }
                    String[] parts = line.split(":", 2);
                    currentTag = parts[0];
                    valueBuilder.setLength(0); // Clear the builder
                    valueBuilder.append(parts[1]);
                } else if (currentTag != null) {
                    valueBuilder.append("\n").append(line);
                }
            }
            if (currentTag != null) {
                addField(doc, rootElement, currentTag, valueBuilder.toString());
            }
        } catch (IOException e) {
            throw new IOException("Error reading text file", e);
        }

        File outputFile = new File(outputFilePath);
        try (FileWriter writer = new FileWriter(outputFile)) {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
        } catch (Exception e) {
            throw new IOException("Error writing XML file", e);
        }

        return outputFile;
    }

    private void addField(Document doc, Element rootElement, String tag, String value) {
        Element field = doc.createElement("field");

        Element tagElement = doc.createElement("tag");
        tagElement.appendChild(doc.createTextNode(tag));
        field.appendChild(tagElement);

        Element valueElement = doc.createElement("value");
        valueElement.appendChild(doc.createTextNode(value.trim()));
        field.appendChild(valueElement);

        rootElement.appendChild(field);
    }

}
