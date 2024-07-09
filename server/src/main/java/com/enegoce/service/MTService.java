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
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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


    ///////////Temp for populating composite PK////////
    //////////////////////////////////////////////////
    private static final AtomicLong idGenerator = new AtomicLong(100);

    public static synchronized String generateString() {
        // Generate a unique string ID based on the current value of idGenerator
        return "S_" + idGenerator.getAndIncrement();
    } //Temp for populating composite PK

    public static synchronized Long generateLong() {
        // Generate a unique string ID based on the current value of idGenerator
        return idGenerator.getAndIncrement();
    } //Temp for populating composite PK

    ////////////////////MT Export ///////////////////////
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
            Settlment settlment = dealService.latestSettlementByDealId(dealId);
            Transport transport = dealService.latestTransportByDealId(dealId);
            List<DealParty> dealPartiesList = dealService.partiesByDealId(dealId);
            List<DealComment> dealCommentsList = dealService.commentsByDealId(dealId);

            if (generateXml) {
                return generateAndExportMtMessageWithXml(infoDeal, settlment, transport, dealPartiesList, dealCommentsList, writer, mappings, mt, true);
            } else {
                return generateAndExportMtMessageWithWriter(infoDeal, settlment, transport, dealPartiesList, dealCommentsList, writer, mappings);
            }
        } catch (Exception e) {
            logger.error("Error generating MT message: " + e);
            return false;
        }
    }

    private boolean generateAndExportMtMessageWithWriter(InfoDeal infoDeal, Settlment settlment, Transport transport, List<DealParty> dealPartiesList, List<DealComment> dealCommentsList, BufferedWriter writer, List<MtFieldMapping> mappings) {
        try {
            //TODO: Add Header
            writer.write("---------------------------- Message Text ----------------------------\r\n");
            processInfoDeal(writer, infoDeal, dealPartiesList, settlment, transport, dealCommentsList, mappings);
            return true;
        } catch (Exception e) {
            logger.error("Error processing data: " + e);
            return false;
        }
    }

    private boolean generateAndExportMtMessageWithXml(InfoDeal infoDeal, Settlment settlment, Transport transport, List<DealParty> dealPartiesList, List<DealComment> dealCommentsList, BufferedWriter writer, List<MtFieldMapping> mappings, String mt, boolean includeXmlHeader) {
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(writer);

            if (includeXmlHeader) {
                xmlWriter.writeStartDocument("UTF-8", "1.0");
                xmlWriter.writeCharacters("\n"); // Ensure new line after XML declaration
            }

            xmlWriter.writeStartElement("MT" + mt);
            xmlWriter.writeCharacters("\n"); // Ensure new line after <MT>

            processInfoDealForXml(xmlWriter, infoDeal, dealPartiesList, settlment, transport, dealCommentsList, mappings);

            xmlWriter.writeEndElement(); // End MT
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

    private void processInfoDealForXml(XMLStreamWriter xmlWriter, InfoDeal infoDeal, List<DealParty> dealPartiesList, Settlment latestSettlment, Transport latestTransport, List<DealComment> dealCommentsList, List<MtFieldMapping> mappings) throws XMLStreamException {
        for (MtFieldMapping mapping : mappings) {
            String entityName = mapping.getEntityName();
            String fieldName = mapping.getDatabaseField();
            String mtTag = mapping.getTag();
            String mappingRule = mapping.getMappingRule();

            if (mappingRule != null && !mappingRule.isEmpty()) {
                processMappingRuleForXml(xmlWriter, mappingRule, mtTag, infoDeal, dealPartiesList, latestSettlment, latestTransport, dealCommentsList);
            } else {
                if (fieldName == null || entityName == null || fieldName.contains("//todo//") || entityName.contains("//todo//")) {
                    continue; // Temporary skip
                }

                String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                Object fieldValue = null;

                try {
                    fieldValue = getFieldValue(entityName, getterMethodName, infoDeal, dealPartiesList, latestSettlment, latestTransport, dealCommentsList, null, null);
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

    private void processMappingRuleForXml(XMLStreamWriter xmlWriter, String mappingRule, String mtTag, InfoDeal infoDeal, List<DealParty> dealPartiesList, Settlment latestSettlment, Transport latestTransport, List<DealComment> dealCommentsList) {
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

                    Object fieldValue = getFieldValue(entity, getterMethodName, infoDeal, dealPartiesList, latestSettlment, latestTransport, dealCommentsList, party, comment);

                    if (fieldValue != null) {
                        if ("DealParty".equals(entity) && "country".equals(field)) {
                            fieldValue = convertCountryCodeToFullName((String) fieldValue);
                        }

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

    private void processInfoDeal(BufferedWriter writer, InfoDeal infoDeal, List<DealParty> dealPartiesList, Settlment latestSettlment, Transport latestTransport, List<DealComment> dealCommentsList, List<MtFieldMapping> mappings) throws IOException {
        for (MtFieldMapping mapping : mappings) {
            String entityName = mapping.getEntityName();
            String fieldName = mapping.getDatabaseField();
            String mtTag = mapping.getTag();
            String mappingRule = mapping.getMappingRule();
            String fieldDescription = mapping.getFieldDescription();

            if (mappingRule != null && !mappingRule.isEmpty()) {
                processMappingRule(writer, mappingRule, mtTag, fieldDescription, infoDeal, dealPartiesList, latestSettlment, latestTransport, dealCommentsList);
            } else {
                if (fieldName == null || entityName == null || fieldName.contains("//todo//") || entityName.contains("//todo//")) {
                    continue;
                }

                String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                Object fieldValue = null;

                try {
                    fieldValue = getFieldValue(entityName, getterMethodName, infoDeal, dealPartiesList, latestSettlment, latestTransport, dealCommentsList, null, null);
                } catch (Exception e) {
                    logger.error("Error accessing field " + fieldName + " in entity " + entityName, e);
                }

                if (fieldValue != null) {
                    writer.write(":" + mtTag + ": " + fieldDescription + "\r\n");
                    writer.write(formatFieldValue(fieldValue) + "\r\n");
                }
            }
        }
    }

    private void processMappingRule(BufferedWriter writer, String mappingRule, String mtTag, String fieldDescription, InfoDeal infoDeal, List<DealParty> dealPartiesList, Settlment settlment, Transport transport, List<DealComment> dealCommentsList) {
        try {
            JSONObject ruleJson = new JSONObject(mappingRule);
            JSONArray fieldsArray = ruleJson.optJSONArray("fields");
            String delimiter = ruleJson.optString("delimiter", "");
            String code = ruleJson.optString("code", null);

            if (fieldsArray != null) {
                StringBuilder combinedValue = new StringBuilder();
                DealParty party = code != null ? dealService.partyByDealIdAndCode(infoDeal.getId(), code) : null;
                DealComment comment = code != null ? dealService.commentByDealAndType(infoDeal.getId(), code) : null;

                if (code != null) {
                    if (party == null) {
                        logger.warn("No DealParty found for code: " + code);
                    } else {
                        logger.warn("No DealComment found for code: " + code);
                    }
                }

                for (int i = 0; i < fieldsArray.length(); i++) {
                    String[] parts = fieldsArray.getString(i).split("\\.");
                    String entity = parts[0];
                    String field = parts[1];
                    String getterMethodName = "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);

                    Object fieldValue = getFieldValue(entity, getterMethodName, infoDeal, dealPartiesList, settlment, transport, dealCommentsList, party, comment);

                    if (fieldValue != null) {
                        if ("DealParty".equals(entity) && "country".equals(field)) {
                            fieldValue = convertCountryCodeToFullName((String) fieldValue);
                        }

                        if (!combinedValue.isEmpty()) {
                            combinedValue.append(delimiter);
                        }
                        combinedValue.append(formatFieldValue(fieldValue));
                    }
                }

                if (!combinedValue.isEmpty()) {
                    writer.write(":" + mtTag + ": " + fieldDescription + "\r\n");
                    writer.write(formatFieldValue(combinedValue) + "\r\n");
                }
            }
        } catch (JSONException e) {
            logger.error("Error parsing mappingRule: " + mappingRule, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getFieldValue(String entityName, String getterMethodName, InfoDeal infoDeal, List<DealParty> dealPartiesList, Settlment settlment, Transport transport, List<DealComment> dealCommentsList, DealParty party, DealComment comment) throws Exception {


        return switch (entityName) {
            case "InfoDeal" -> infoDeal.getClass().getMethod(getterMethodName).invoke(infoDeal);
            case "Settlment" -> settlment.getClass().getMethod(getterMethodName).invoke(settlment);
            case "Transport" -> transport.getClass().getMethod(getterMethodName).invoke(transport);
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

    private String formatFieldValue(Object fieldValue) {
        if (fieldValue instanceof Date date) {
            return new java.text.SimpleDateFormat("yyMMdd").format(date);
        } else if (fieldValue instanceof java.time.LocalDate) {
            return ((java.time.LocalDate) fieldValue).format(DATE_FORMATTER);
        } else {
            return fieldValue.toString();
        }
    }

    private String convertCountryCodeToFullName(String countryCode) {
        Locale locale = new Locale("", countryCode);
        return locale.getDisplayCountry(Locale.ENGLISH);
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
            Settlment settlment = dealService.latestSettlementByDealId(dealId);
            Transport transport = dealService.latestTransportByDealId(dealId);
            List<DealParty> dealPartiesList = dealService.partiesByDealId(dealId);
            List<DealComment> dealCommentsList = dealService.commentsByDealId(dealId);

            if ("txt".equalsIgnoreCase(format)) {
                writer.write("12:" + mt + "\r\n");
                writer.write("=========================\r\n");
                return generateAndExportMtMessageWithWriter(infoDeal, settlment, transport, dealPartiesList, dealCommentsList, writer, mappings);
            } else if ("xml".equalsIgnoreCase(format)) {
                return generateAndExportMt798MessageWithXml(infoDeal,settlment,transport,dealPartiesList,dealCommentsList,writer,mappings,mt );
            } else {
                logger.error("Unsupported output format: " + format);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error generating MT798: " + e);
            return false;
        }
    }

    private boolean generateAndExportMt798MessageWithXml(InfoDeal infoDeal, Settlment settlment, Transport transport, List<DealParty>dealPartiesList, List<DealComment> dealCommentsList, BufferedWriter writer, List<MtFieldMapping> mappings, String mt) {
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
            generateAndExportMtMessageWithXml(infoDeal, settlment, transport, dealPartiesList, dealCommentsList, writer, mappings, mt, false);


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

    ///////////////////// MT Import //////////////////////
    //////////////////////////////////////////////////////

    public Map<String, String> parseMtMessage(File file, String mt) {
        Map<String, String> fieldsMap = new HashMap<>();

        try {
            String mtXml = parseFile(file, mt);

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

                // Directly put value in fieldsMap
                fieldsMap.put(tag, value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldsMap;
    }

    public boolean importMT(File file, String mt) {
        Map<String, String> parsedMessage = parseMtMessage(file, mt);

        List<MtFieldMapping> mappings = mappingService.mappingsByMt(mt);
        if (mappings.isEmpty()) {
            logger.error("No mappings found for mt: " + mt);
            return false;
        }

        if (mt.equals("700")) {
            InfoDealDto infoDealDto = new InfoDealDto();
            List<DealCommentDto> comments = new ArrayList<>();
            List<DealGoodsDto> goods = new ArrayList<>();
            List<DealPartyDto> parties = new ArrayList<>();
            List<SettlementDto> settlements = new ArrayList<>();

            // Initialize an index for iterating through goodsDescs
            int goodsIndex = 0;

            for (Map.Entry<String, String> entry : parsedMessage.entrySet()) {
                String tag = entry.getKey();
                String value = entry.getValue();

                switch (tag) {
                    case "40A":
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
                    case "50":
                    case "51a":
                    case "59":
                    case "58a":
                    case "53a":
                    case "57a":
                        // Split concatenated fields by ",\n"
                        String[] fields = value.split(",");
                        if (fields.length >= 6) {
                            DealPartyDto dealPartyDto = new DealPartyDto();
                            String codPrt = getCodPrtForTag(tag); // Replace with your logic to determine codPrt
                            dealPartyDto.setId(new DealPartyPKID(infoDealDto.getId(), generateLong(), codPrt));
                            dealPartyDto.setNom(fields[0]);
                            dealPartyDto.setStreet1(fields[1]);
                            dealPartyDto.setStreet2(fields[2]);
                            dealPartyDto.setStreet3(fields[3]);
                            dealPartyDto.setTown(fields[4]);

                            // Convert country name to ISO code
                            String countryCode = getCountryCode(fields[5].trim());
                            if (countryCode != null) {
                                dealPartyDto.setCountry(countryCode);
                            } else {
                                logger.warn("Country code not found for country: " + fields[5]);
                            }
                            parties.add(dealPartyDto);
                        } else {
                            logger.warn("Insufficient fields for tag " + tag + ": " + value);
                        }
                        break;
                    case "47A":
                    case "49G":
                    case "49H":
                    case "71D":
                    case "78":
                    case "72Z":
                        DealCommentDto commentDto = new DealCommentDto();
                        commentDto.setId(new DealCommentPKID(infoDealDto.getId(), generateLong()));
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
                    case "42M":
                    case "42P":
                        String[] mixed = value.split(",");

                        // Ensure settlements list has enough DTOs
                        while (settlements.size() < mixed.length) {
                            SettlementDto settDto = new SettlementDto();
                            settDto.setId(infoDealDto.getId());
                            settlements.add(settDto);
                        }

                        for (int j = 0; j < mixed.length; j++) {
                            SettlementDto dto = settlements.get(j);
                            switch (tag) {
                                case "42M":
                                    String setterMethodNamePay = "setMixedPay" + (j + 1);
                                    try {
                                        Method setterMethod = dto.getClass().getMethod(setterMethodNamePay, String.class);
                                        setterMethod.invoke(dto, mixed[j]);
                                    } catch (NoSuchMethodException | IllegalAccessException |
                                             InvocationTargetException e) {
                                        e.printStackTrace(); // Handle exceptions appropriately
                                    }
                                    break;
                                case "42P":
                                    String setterMethodNameNeg = "setNegDefPay" + (j + 1);
                                    try {
                                        Method setterMethod = dto.getClass().getMethod(setterMethodNameNeg, String.class);
                                        setterMethod.invoke(dto, mixed[j]);
                                    } catch (NoSuchMethodException | IllegalAccessException |
                                             InvocationTargetException e) {
                                        e.printStackTrace(); // Handle exceptions appropriately
                                    }
                                    break;
                            }
                        }
                        break;
                    case "43P":
                        infoDealDto.setPartialTranshipment(value);
                        break;
                    case "43T":
                        infoDealDto.setTranshipment(value);
                        break;
                    case "45A":
                        // Reset goodsIndex to start from 0
                        goodsIndex = 0;
                        // Process goods descriptions (assuming value is a single string separated by '+')
                        String[] goodsDescs = value.split("\\s*\\+\\s*");
                        goodsDescs = Arrays.stream(goodsDescs)
                                .filter(desc -> !desc.trim().isEmpty())
                                .toArray(String[]::new);
                        System.out.println(Arrays.toString(goodsDescs));

                        for (String goodsDesc : goodsDescs) {
                            if (goodsIndex < goods.size()) {
                                // Update existing DealGoodsDto with goodsDesc
                                DealGoodsDto dto = goods.get(goodsIndex);
                                dto.setGoodsDesc(goodsDesc.trim()); // Trim to remove any leading/trailing whitespace
                                logger.info("Updated DealGoodsDto with goodsDesc at index " + goodsIndex);
                                goodsIndex++; // Move to the next DealGoodsDto
                            } else {
                                logger.warn("More goods descriptions provided than expected. Current goodsIndex: " + goodsIndex + ", goods.size(): " + goods.size());
                                break;
                            }
                        }
                        break;
                    case "44A":
                    case "44E":
                    case "44F":
                    case "44B":
                    case "44C":
                    case "44D":
                        String[] splitValues = value.split(", ");

                        // Ensure goods list is long enough to update
                        while (goods.size() < splitValues.length) {
                            DealGoodsDto goodDto = new DealGoodsDto();
                            goodDto.setId(new DealGoodsPKID(infoDealDto.getId(), generateString(), generateLong()));
                            goods.add(goodDto);
                            logger.info("Added new DealGoodsDto. Current goods.size(): " + goods.size());
                        }

                        // Iterate through split values and update goods list
                        for (int j = 0; j < splitValues.length; j++) {
                            if (j < goods.size()) {
                                DealGoodsDto dto = goods.get(j);
                                logger.info("Updating DealGoodsDto at index " + j + " for tag " + tag);

                                switch (tag) {
                                    case "44A":
                                        dto.setPlaceOfTakingCharge(splitValues[j]);
                                        break;
                                    case "44E":
                                        dto.setPortOfLoading(splitValues[j]);
                                        break;
                                    case "44F":
                                        dto.setPortOfDischarge(splitValues[j]);
                                        break;
                                    case "44B":
                                        dto.setPlaceOfFinalDestination(splitValues[j]);
                                        break;
                                    case "44C":
                                        String shipmentDateString = splitValues[j].trim(); // Trim to remove any leading/trailing whitespace
                                        try {
                                            Date shipmentDate = new SimpleDateFormat("yyMMdd").parse(shipmentDateString);
                                            dto.setShipmentDateLast(shipmentDate);
                                        } catch (ParseException e) {
                                            logger.error("Error parsing shipment date for tag " + tag + ": " + shipmentDateString);
                                            // Handle parsing error as needed
                                        }
                                        break;
                                    case "44D":
                                        try {
                                            Integer shipmentPeriod = Integer.parseInt(splitValues[j]);
                                            dto.setShipmentPeriod(shipmentPeriod);
                                        } catch (NumberFormatException e) {
                                            logger.error("Invalid integer format for tag " + tag + ": " + splitValues[j]);
                                            // Handle error or skip if necessary
                                        }
                                        break;
                                    default:
                                        logger.warn("Unhandled tag " + tag + " in switch statement");
                                        break;
                                }
                            } else {
                                logger.warn("More values provided than expected for tag " + tag + ". Current index: " + j + ", goods.size(): " + goods.size());
                                break;
                            }
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

            while (goodsIndex < goods.size()) {
                DealGoodsDto dto = goods.get(goodsIndex);
                logger.warn("No corresponding goods description found for DealGoodsDto ID: " + dto.getId());
                goodsIndex++;
            }

            Long infoDealId = dealService.saveInfoDeal(infoDealDto);
            dealService.saveDealCommentList(comments, infoDealId);
            dealService.saveSettlementList(settlements, infoDealId);
            dealService.saveDealGoodsList(goods, infoDealId);
            dealService.saveDealPartyList(parties, infoDealId);


            return true;
        } else if (mt.equals("701")) {
            Long idDeal = null; // Initialize idDeal
            List<DealCommentDto> comments = new ArrayList<>();

            // Iterate over parsedMessage to find tag 20 and extract idDeal
            for (Map.Entry<String, String> entry : parsedMessage.entrySet()) {
                String tag = entry.getKey();
                String value = entry.getValue();

                if ("20".equals(tag)) {
                    // Parse value of tag 20 to Long
                    try {
                        idDeal = Long.parseLong(value);
                    } catch (NumberFormatException e) {
                        logger.error("Error parsing idDeal from tag 20: {}", e.getMessage());
                        return false; // or handle error appropriately
                    }
                } else if ("45B".equals(tag) || "46B".equals(tag) || "47B".equals(tag)) {
                    DealCommentDto commentDto = new DealCommentDto();
                    commentDto.setId(new DealCommentPKID(idDeal, generateLong()));
                    commentDto.setComment(value);

                    switch (tag) {
                        case "45B":
                            commentDto.setTypeComt("47A");
                            break;
                        case "46B":
                        case "47B":
                            commentDto.setTypeComt("46B");
                            break;
                    }

                    comments.add(commentDto);
                } else {
                    logger.error("Unsupported Message");
                }
            }

            // Save the comments with associated idDeal
            if (idDeal != null) {
                dealService.saveDealCommentList(comments, idDeal);
                return true;
            } else {
                logger.error("No idDeal found in message");
                return false; // or handle the case where idDeal is not found
            }
        }
        return false;
    }

    private String getCodPrtForTag(String tag) {
        return switch (tag) {
            case "50" -> "APP";
            case "51a" -> "ISB";
            case "59" -> "BNE";
            case "58a" -> "CONF";
            case "53a" -> "RMB";
            case "57a" -> "ADT";
            default -> ""; // Default case
        };
    }

    public String getCountryCode(String countryName) {
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            if (countryName.equalsIgnoreCase(locale.getDisplayCountry(Locale.ENGLISH))) {
                return locale.getCountry();
            }
        }
        return null;
    }

    //////////////////////String to Valid XML///////////////
    ///////////////////////////////////////////////////////

    public String parseFile(File file, String mt) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                contentBuilder.append(currentLine).append("\n");
                logger.info(contentBuilder.toString());
            }
        }
        return convertTextToXml(contentBuilder.toString(), mt);
    }

    public String convertTextToXml(String message, String mt) {
        StringBuilder xmlBuilder = new StringBuilder();

        // Append root element
        xmlBuilder.append("<MT").append(mt).append(">");

        String[] lines = message.split("\n");
        String currentTag = null;
        StringBuilder currentValue = new StringBuilder();
        boolean tagFound = false;

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                line = line.trim();
                if (line.startsWith(":")) {
                    // Found a tag line
                    if (currentTag != null) {
                        // Append previous tag and value
                        appendField(xmlBuilder, currentTag, currentValue.toString().trim());
                        currentValue.setLength(0); // Clear the current value
                        tagFound = false; // Reset tagFound
                    }
                    // Extract tag (2 or 3 characters until the next ':')
                    int endIndex = line.indexOf(':', 1);
                    if (endIndex > 0) {
                        currentTag = line.substring(1, endIndex).trim();
                        tagFound = true; // Set tagFound to true
                    } else {
                        currentTag = line.substring(1).trim();
                    }
                } else if (tagFound) {
                    // Accumulate value after tag line
                    currentValue.append(line);
                }
            }
        }

        // Append the last tag and value
        if (currentTag != null && currentValue.length() > 0) {
            appendField(xmlBuilder, currentTag, currentValue.toString().trim());
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
