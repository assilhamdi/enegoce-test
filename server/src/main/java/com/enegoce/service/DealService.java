package com.enegoce.service;

import com.enegoce.entities.*;
import com.enegoce.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.time.format.DateTimeFormatter;

@Service
public class DealService {

    @Autowired
    private InfoDealRepository dealRepo;

    @Autowired
    private MtFieldMappingRepository mappingRepo;

    @Autowired
    private DealGoodsRepository goodsRepo;

    @Autowired
    private DealPartyRepository partiesRepo;

    @Autowired
    private DealCommentRepository commRepo;

    @Autowired
    private SettlementRepository settRepo;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    private static final Logger logger = LogManager.getLogger(DealService.class);


    /*public boolean generateAndExportMt798Message(Integer dealId, String mt, String filePath) {
        if (!"700".equals(mt) && !"701".equals(mt)) {
            logger.error("Unsupported MT for MT798: " + mt);
            return false;
        }

        List<MtFieldMapping> mappings = mappingRepo.findByMt(mt);
        if (mappings.isEmpty()) {
            logger.error("No mappings found for mt: " + mt);
            return false;
        }

        mappings.sort(Comparator.comparingInt(MtFieldMapping::getFieldOrder));
        logger.info(mappings);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("12:" + mt + "\r\n");
            writer.write("=========================\r\n");

            return generateAndExportMtMessageWithWriter(dealId, mt, writer, mappings);
        } catch (Exception e) {
            logger.error("Error generating MT798: " + e);
            return false;
        }
    }*/

    //TODO: XML/TXT to database
    //TODO: MT 798

    ////////////////////MT Generation////////////////////
    /////////////////////////////////////////////////////

    public boolean generateAndExportMtMessage(Integer dealId, String mt, String filePath, boolean generateXml) {
        List<MtFieldMapping> mappings = mappingRepo.findByMt(mt);
        if (mappings.isEmpty()) {
            logger.error("No mappings found for mt: " + mt);
            return false;
        }

        mappings.sort(Comparator.comparingInt(MtFieldMapping::getFieldOrder));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            InfoDeal infoDeal = this.dealById(dealId);
            List<DealGoods> dealGoodsList = this.goodsByDealId(dealId);
            List<DealParty> dealPartiesList = this.partiesByDealId(dealId);
            List<Settlement> settlementList = this.settlementsByDealId(dealId);
            List<DealComment> dealCommentsList = this.commentsByDealId(dealId);

            if (generateXml) {
                return generateAndExportMtMessageWithXml(infoDeal, dealGoodsList, dealPartiesList, settlementList, dealCommentsList, writer, mappings);
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

    private boolean generateAndExportMtMessageWithXml(InfoDeal infoDeal, List<DealGoods> dealGoodsList, List<DealParty> dealPartiesList, List<Settlement> settlementList, List<DealComment> dealCommentsList, BufferedWriter writer, List<MtFieldMapping> mappings) {
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(writer);

            xmlWriter.writeStartDocument("UTF-8", "1.0");
            xmlWriter.writeCharacters("\n"); // Ensure new line after XML declaration
            xmlWriter.writeStartElement("MT700");
            xmlWriter.writeCharacters("\n"); // Ensure new line after <MT700>

            processInfoDealForXml(xmlWriter, infoDeal, dealGoodsList, dealPartiesList, settlementList, dealCommentsList, mappings);

            xmlWriter.writeEndElement(); // End MT700
            xmlWriter.writeCharacters("\n"); // Ensure new line after </MT700>
            xmlWriter.writeEndDocument();
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
                DealParty party = code != null ? partyByDealIdAndCode(infoDeal.getId(), code) : null;
                DealComment comment = code != null ? commentByDealAndType(infoDeal.getId(), code) : null;

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
                DealParty party = code != null ? partyByDealIdAndCode(infoDeal.getId(), code) : null;
                DealComment comment = code != null ? commentByDealAndType(infoDeal.getId(), code) : null;

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
                    writer.write(mtTag + ":" + combinedValue.toString() + "\r\n");
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
        if ("goodsDesc".equals(fieldName)) {
            StringBuilder goodsValues = new StringBuilder();
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
            return !goodsValues.isEmpty() ? goodsValues.toString() : null;
        } else {
            StringBuilder goodsValues = new StringBuilder();
            for (DealGoods dealGoods : dealGoodsList) {
                Object goodsValue = dealGoods.getClass().getMethod(getterMethodName).invoke(dealGoods);
                if (goodsValue != null) {
                    if (!goodsValues.isEmpty()) {
                        goodsValues.append(", ");  // Adjust delimiter as needed
                    }
                    goodsValues.append(formatFieldValue(goodsValue));
                }
            }
            return !goodsValues.isEmpty() ? goodsValues.toString() : null;
        }
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

    ////////////////////InfoDeal/////////////////////
    /////////////////////////////////////////////////

    public List<InfoDeal> deals() {
        return dealRepo.findAll();
    }

    public InfoDeal dealById(Integer id) {
        Optional<InfoDeal> deal = dealRepo.findById(id);
        return deal.orElse(null);
    }


    ////////////////////DealGoods////////////////////
    /////////////////////////////////////////////////

    public List<DealGoods> goods() {
        return goodsRepo.findAll();
    }

    public DealGoods dealGoodsById(Integer id) {
        Optional<DealGoods> deal = goodsRepo.findById(id);
        return deal.orElse(null);
    }

    public List<DealGoods> goodsByDealId(Integer id) {
        return goodsRepo.findGoodsByDealId(id);
    }


    ////////////////////DealParty////////////////////
    /////////////////////////////////////////////////

    public List<DealParty> parties() {
        return partiesRepo.findAll();
    }

    public DealParty dealPartyById(Integer id) {
        Optional<DealParty> party = partiesRepo.findById(id);
        return party.orElse(null);
    }

    public List<DealParty> partiesByDealId(Integer id) {
        return partiesRepo.findPartiesByDealId(id);
    }

    public DealParty partyByDealIdAndCode(Integer id, String code) {
        return partiesRepo.findPartyByDealIdAndCode(id, code);
    }

    ////////////////////Comment//////////////////////
    /////////////////////////////////////////////////

    public List<DealComment> comments() {
        return commRepo.findAll();
    }

    public DealComment commentById(Integer id) {
        Optional<DealComment> comment = commRepo.findById(id);
        return comment.orElse(null);
    }

    public List<DealComment> commentsByDealId(Integer id) {
        return commRepo.findCommentsByDealId(id);
    }

    public DealComment commentByDealAndType(Integer id, String type) {
        return commRepo.findCommentByDealAndType(id, type);
    }

    ////////////////////Settlement////////////////////
    /////////////////////////////////////////////////

    public List<Settlement> settlements() {
        return settRepo.findAll();
    }

    public Settlement settlementById(Integer id) {
        Optional<Settlement> settlement = settRepo.findById(id);
        return settlement.orElse(null);
    }

    public List<Settlement> settlementsByDealId(Integer id) {
        return settRepo.findSettlementsByDealId(id);
    }


}
