package com.enegoce.service;

import com.enegoce.entities.*;
import com.engoce.deal.dto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MTService {

    @Autowired
    private MtFieldMappingService mappingService;

    @Autowired
    private DealService dealService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
    private static final DateTimeFormatter REVERSE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger logger = LogManager.getLogger(DealService.class);
    private AtomicLong idGenerator = new AtomicLong(100);


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


    ///////////////////// XML to Database ////////////////
    //////////////////////////////////////////////////////

    public Map<String, String> parseMtMessage(String mtMessage, String mt) {
        Map<String, String> fieldsMap = new HashMap<>();

        try {
            // Convert mtMessage to XML
            String mtXml = convertTextToXml(mtMessage, mt);

            // Parse the XML document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(mtXml)));

            // Extract fields from XML
            NodeList fieldNodes = doc.getElementsByTagName("field");
            for (int i = 0; i < fieldNodes.getLength(); i++) {
                Element fieldElement = (Element) fieldNodes.item(i);
                String tag = fieldElement.getElementsByTagName("tag").item(0).getTextContent().trim();
                String value = fieldElement.getElementsByTagName("value").item(0).getTextContent().trim();

                logger.info("tag : " + tag);
                logger.info("value : " + value);

                // Directly put value in fieldsMap assuming each tag is unique
                fieldsMap.put(tag, value);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Handle parsing exceptions
        }

        return fieldsMap;
    }

    public void importMT(String mtMessage, String mt) {
        Map<String, String> parsedMessage = parseMtMessage(mtMessage, mt);

        List<MtFieldMapping> mappings = mappingService.mappingsByMt(mt);
        if (mappings.isEmpty()) {
            logger.error("No mappings found for mt: " + mt);
            return;
        }

        if (mt.equals("700")) {
            InfoDealDto infoDealDto = new InfoDealDto();
            List<DealCommentDto> comments = new ArrayList<>();
            List<DealGoodsDto> goods = new ArrayList<>();
            List<DealPartyDto> parties = new ArrayList<>();
            List<SettlementDto> settlements = new ArrayList<>();

            for (Map.Entry<String, String> entry : parsedMessage.entrySet()) {
                String tag = entry.getKey();
                String value = entry.getValue();

                switch (tag) {
                    case "20":
                        // Handle reference number
                        infoDealDto.setFormLC(value);
                        break;
                    case "31C":
                        if (!value.isEmpty()) {
                            LocalDate localDate = LocalDate.parse(value, DATE_FORMATTER);
                            String formattedDate = localDate.format(REVERSE_DATE_FORMATTER);
                            infoDealDto.setDueDate(LocalDate.parse(formattedDate));  // Assuming InfoDealDto has a setDueDate method
                            logger.info("31C dueDate set to: " + formattedDate);
                        }
                        break;
                    case "31D":
                        // Extract expiryDate and expiryPlace
                        String expiryDateStr = value.substring(0, 6); // First 6 characters for expiryDate
                        String expiryPlace = value.substring(6); // Remainder for expiryPlace

                        // Parse expiryDate from yyMMdd to yyyy-MM-dd
                        LocalDate localDate = LocalDate.parse(expiryDateStr, DATE_FORMATTER);
                        String formattedDate = localDate.format(REVERSE_DATE_FORMATTER);
                        infoDealDto.setExpiryDate(LocalDate.parse(formattedDate));
                        infoDealDto.setExpiryPlace(expiryPlace);
                        break;
                    case "51a":
                        infoDealDto.setBankISSRef(value);
                        break;
                    case "50":
                    case "59":
                    case "58a":
                    case "53a":
                    case "57a":
                        // Split concatenated fields by ",\n"
                        String[] fields = value.split(", ");
                        if (fields.length >= 6) {
                            DealPartyDto dealPartyDto = new DealPartyDto();
                            String codPrt = getCodPrtForTag(tag); // Replace with your logic to determine codPrt
                            dealPartyDto.setId(new DealPartyPKID(infoDealDto.getId(), idGenerator.getAndIncrement(), codPrt));
                            dealPartyDto.setNom(fields[0]);
                            dealPartyDto.setStreet1(fields[1]);
                            dealPartyDto.setStreet2(fields[2]);
                            dealPartyDto.setStreet3(fields[3]);
                            dealPartyDto.setTown(fields[4]);
                            dealPartyDto.setCountry(fields[5]);

                            // Add dealPartyDto to your list or process as needed
                            parties.add(dealPartyDto);
                        } else {
                            logger.warn("Insufficient fields for tag 50: " + value);
                        }
                        break;
                    case "47A":
                    case "46G":
                    case "49H":
                    case "71D":
                    case "78":
                    case "72Z":
                        DealCommentDto commentDto = new DealCommentDto();
                        commentDto.setId(new DealCommentPKID(infoDealDto.getId(), idGenerator.getAndIncrement()));
                        commentDto.setComment(value);
                        commentDto.setTypeComt(tag);
                        comments.add(commentDto);
                        break;
                    case "32B":
                        // Concatenated fields without delimiter
                        if (value.length() >= 3) {
                            String currencyID = value.substring(0, 3);
                            String lcAmountStr = value.substring(3);
                            infoDealDto.setCurrencyID(currencyID);
                            logger.info("CurrencyID set to: " + currencyID);

                            // Convert lcAmount to BigDecimal
                            BigDecimal lcAmount = new BigDecimal(lcAmountStr);
                            infoDealDto.setLcAmount(lcAmount);
                            logger.info("LcAmount set to: " + lcAmount);
                        } else {
                            logger.warn("Insufficient fields for tag 32B: " + value);
                        }
                        break;
                    case "39A":
                        BigDecimal varAmountTolerance = new BigDecimal(value.replaceAll("[^\\d.,]", ""));
                        infoDealDto.setVarAmountTolerance(varAmountTolerance);
                        break;
                    case "39C":
                        infoDealDto.setAddAmtCovered(value);
                        break;
                    case "41A":
                        String[] fields2 = value.split(", ");
                        if (fields2.length >= 2) {
                            SettlementDto settDto = new SettlementDto();
                            settDto.setAvailableWithBank(fields2[0]);
                            settDto.setAvailableWithOther(fields2[1]);
                            settlements.add(settDto);
                            break;

                        } else {
                            logger.warn("Insufficient fields for tag 50: " + value);
                        }
                        break;
                    case "42C":
                        infoDealDto.setDraftAt(value);
                        break;
                    case "42a":
                        infoDealDto.setDraft(value);
                        break;
                    case "43P":
                        infoDealDto.setPartialTranshipment(value);
                        break;
                    case "43T":
                        infoDealDto.setTranshipment(value);
                        break;
                    case "45A":
                        // Concatenated goods descriptions starting with "+"
                        String[] goodsDescs = value.split("\\+");
                        for (String goodsDesc : goodsDescs) {
                            DealGoodsDto goodDto = new DealGoodsDto();
                            if (!goodsDesc.trim().isEmpty()) {
                                String trimmedDesc = goodsDesc.trim();
                                goodDto.setGoodsDesc(trimmedDesc);
                                goods.add(goodDto);
                            }
                        }
                        break;
                    case "44A":
                    case "44E":
                    case "44F":
                    case "44B":
                        // Concatenated values separated by ", "
                        String[] splitValues = value.split(", ");
                        if (splitValues.length > 0) {
                            for (int j = 0; j < splitValues.length; j++) {
                                if (j < goods.size()) {
                                    // Set the corresponding value in the existing DealGoodsDto
                                    switch (tag) {
                                        case "44A":
                                            goods.get(j).setPlaceOfTakingCharge(splitValues[j]);
                                            break;
                                        case "44E":
                                            goods.get(j).setPortOfLoading(splitValues[j]);
                                            break;
                                        case "44F":
                                            goods.get(j).setPortOfDischarge(splitValues[j]);
                                            break;
                                        case "44B":
                                            goods.get(j).setPlaceOfFinalDestination(splitValues[j]);
                                            break;
                                    }
                                } else {
                                    logger.warn("More split values than DealGoodsDto objects for tag " + tag);
                                    break; // Exit loop or handle as appropriate
                                }
                            }
                        } else {
                            logger.warn("No split values found for tag " + tag);
                        }
                        break;

                    case "44C":
                        // Concatenated values separated by ", "
                        String[] splitValuesC = value.split(", ");
                        if (splitValuesC.length > 0) {
                            for (int j = 0; j < splitValuesC.length; j++) {
                                if (j < goods.size()) {
                                    // Convert to Date using SimpleDateFormat
                                    try {
                                        Date shipmentDate = new SimpleDateFormat("yyyyMMdd").parse(splitValuesC[j]);
                                        goods.get(j).setShipmentDateLast(shipmentDate);
                                    } catch (ParseException e) {
                                        logger.error("Error parsing shipment date for tag " + tag + ": " + splitValuesC[j]);
                                        // Handle parsing error as needed
                                    }
                                } else {
                                    logger.warn("More split values than DealGoodsDto objects for tag " + tag);
                                    break;
                                }
                            }
                        } else {
                            logger.warn("No split values found for tag " + tag);
                        }
                        break;

                    case "44D":
                        // Concatenated values separated by ", "
                        String[] splitValuesD = value.split(", ");
                        if (splitValuesD.length > 0) {
                            for (int j = 0; j < splitValuesD.length; j++) {
                                if (j < goods.size()) {
                                    // Convert to Integer
                                    try {
                                        Integer shipmentPeriod = Integer.parseInt(splitValuesD[j]);
                                        goods.get(j).setShipmentPeriod(shipmentPeriod);
                                    } catch (NumberFormatException e) {
                                        logger.error("Invalid integer format for tag " + tag + ": " + splitValuesD[j]);
                                        // Handle error or skip if necessary
                                    }
                                } else {
                                    logger.warn("More split values than DealGoodsDto objects for tag " + tag);
                                    break;
                                }
                            }
                        } else {
                            logger.warn("No split values found for tag " + tag);
                        }
                        break;
                    case "46A":
                        infoDealDto.setDocument(value);
                        break;
                    case "48":
                        try {
                            Integer presDay = Integer.parseInt((value));
                            infoDealDto.setPresDay(presDay);
                        } catch (NumberFormatException e) {
                            logger.error("Invalid integer format for tag " + tag + ": " + value);
                        }
                        break;
                    case "49":
                        infoDealDto.setConfirmationCharge(value);
                        break;
                }
            }
            dealService.saveInfoDeal(infoDealDto);
            dealService.saveDealGoodsList(goods);
            dealService.saveDealPartyList(parties);
            dealService.saveDealCommentList(comments);
            dealService.saveSettlementList(settlements);
        }
    }

    private String getCodPrtForTag(String tag) {
        return switch (tag) {
            case "50" -> "APP";
            case "59" -> "BNE";
            case "58a" -> "CONF";
            case "53a" -> "RMB";
            case "57a" -> "ADT";
            default -> ""; // Default case
        };
    }

    public String test(String m, String mt) {
        Map<String, String> map = parseMtMessage(m, mt);
        return "test";
    }

    //TODO: XML to database

    //////////////////////String to Valid XML///////////////
    ///////////////////////////////////////////////////////

    public String convertTextToXml(String message, String mt) throws IOException {
        StringBuilder xmlBuilder = new StringBuilder();

        // Append root element
        xmlBuilder.append("<MT").append(mt).append(">");

        String[] lines = message.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                String[] keyValuePairs = line.split("\\s+(?=[0-9A-Za-z]+:)"); // Split by whitespace before a tag
                for (String pair : keyValuePairs) {
                    String[] parts = pair.split(":", 2);
                    String tag = parts[0].trim();
                    String value = parts[1].trim();

                    appendField(xmlBuilder, tag, value);
                }
            }
        }

        // Close root element
        xmlBuilder.append("</MT").append(mt).append(">");

        return xmlBuilder.toString();
    }

    private void appendField(StringBuilder xmlBuilder, String tag, String value) {
        xmlBuilder.append("<field>");
        xmlBuilder.append("<tag>").append(tag).append("</tag>");
        xmlBuilder.append("<value>").append(value).append("</value>");
        xmlBuilder.append("</field>");
    }

}
