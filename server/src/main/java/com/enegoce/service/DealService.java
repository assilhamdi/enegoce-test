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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
    private SettlementRepository settRepo;

    private static final Logger logger = LogManager.getLogger(DealService.class);

    public List<InfoDeal> deals() {
        return dealRepo.findAll();
    }

    public InfoDeal dealById(Integer id) {
        Optional<InfoDeal> deal = dealRepo.findById(id);
        return deal.orElse(null);
    }

    public InfoDeal createInfoDeal(InfoDealInput dealInput) {
        InfoDeal deal = new InfoDeal();

        deal.setFormLC(dealInput.formLC());
        deal.setCustomerReference(dealInput.customerReference());
        deal.setCounterParty(dealInput.counterParty());
        deal.setBankISSRef(dealInput.bankISSRef());
        deal.setBankRMBRef(dealInput.bankRMBRef());
        deal.setCurrencyID(dealInput.currencyId());
        deal.setPartialTranshipment(dealInput.partialTranshipment());
        deal.setTranshipment(dealInput.transhipment());
        deal.setPresDay(dealInput.presDay());
        deal.setConfirmationCharge(dealInput.confirmationCharge());
        deal.setAddAmtCovered(dealInput.addAmtCovered());

        try {
            deal.setDueDate(new SimpleDateFormat("yyyy-MM-dd").parse(dealInput.dueDate()));
            if (dealInput.expiryDate() != null) { // Optional expiryDate
                deal.setExpiryDate(new SimpleDateFormat("yyyy-MM-dd").parse(dealInput.expiryDate()));
            }
        } catch (ParseException e) {
            // Handle parsing exception (e.g., throw custom exception or log error)
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd.");
        }
        deal.setExpiryPlace(dealInput.expiryPlace());


        try {
            deal.setCreationDate(new Timestamp(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(dealInput.creationDate()).getTime()));
        } catch (ParseException e) {
            // Handle parsing exception (e.g., set default creationDate or log error)
            deal.setCreationDate(new Timestamp(System.currentTimeMillis()));
        }

        deal.setLcAmount(new BigDecimal(dealInput.lcAmount().replace(",", "")));
        deal.setVarAmountTolerance(new BigDecimal(dealInput.varAmountTolerance().replace(",", "")));

        return dealRepo.save(deal);

    }

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

    ////////////////////MT Generation////////////////////
    /////////////////////////////////////////////////////

    public boolean generateAndExportMtMessage(Integer dealId, String mt, String filePath) {
        // Retrieve field mappings for the specified message type
        List<MtFieldMapping> mappings = mappingRepo.findByMt(mt);
        if (mappings.isEmpty()) {
            // Log error and return false if no mappings are found
            logger.error("No mappings found for mt: " + mt);
            return false;
        }

        // Sort the mappings based on the field order
        mappings.sort(Comparator.comparingInt(MtFieldMapping::getFieldOrder));
        logger.info(mappings);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Retrieve data related to the deal
            InfoDeal infoDeal = this.dealById(dealId);
            List<DealGoods> dealGoodsList = this.goodsByDealId(dealId);
            List<DealParty> dealPartiesList = this.partiesByDealId(dealId);
            List<Settlement> settlementList = this.settlementsByDealId(dealId);

            // Generate and export MT message using the writer and mappings
            return generateAndExportMtMessageWithWriter(infoDeal, dealGoodsList, dealPartiesList, settlementList, writer, mappings);
        } catch (Exception e) {
            // Log error and return false if an exception occurs
            logger.error("Error generating MT message: " + e);
            return false;
        }
    }

    private boolean generateAndExportMtMessageWithWriter(InfoDeal infoDeal, List<DealGoods> dealGoodsList, List<DealParty> dealPartiesList, List<Settlement> settlementList, BufferedWriter writer, List<MtFieldMapping> mappings) {
        try {
            // Process and write the MT message details
            processInfoDeal(writer, infoDeal, dealGoodsList, dealPartiesList, settlementList, mappings);
            return true; // Return true if writing is successful
        } catch (Exception e) {
            // Log error and return false if an exception occurs
            logger.error("Error processing data: " + e);
            return false; // Return false if processing data fails
        }
    }

    private void processInfoDeal(BufferedWriter writer, InfoDeal infoDeal, List<DealGoods> dealGoodsList, List<DealParty> dealPartiesList, List<Settlement> settlementList, List<MtFieldMapping> mappings) throws IOException {
        for (MtFieldMapping mapping : mappings) {
            // Extract entity name, field name, tag, and mapping rule from the mapping
            String entityName = mapping.getEntityName();
            String fieldName = mapping.getDatabaseField();
            String mtTag = mapping.getTag();
            String mappingRule = mapping.getMappingRule();

            if (mappingRule != null && !mappingRule.isEmpty()) {
                // Process mapping rule if it exists
                processMappingRule(writer, mappingRule, mtTag, infoDeal, dealGoodsList, dealPartiesList, settlementList);
            } else {
                // Process single field if no mapping rule exists
                if (fieldName == null || entityName == null) {
                    continue;
                }

                if (fieldName.contains("//todo//") || entityName.contains("//todo//")) { // TODO: Temporary
                    continue;
                }

                // Construct the getter method name for the field
                String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                Object fieldValue = null;

                try {
                    // Retrieve the field value based on the entity name
                    if ("InfoDeal".equals(entityName)) {
                        fieldValue = infoDeal.getClass().getMethod(getterMethodName).invoke(infoDeal);
                    } else if ("DealGoods".equals(entityName)) {
                        fieldValue = getFieldValueFromList(dealGoodsList, getterMethodName);
                    } else if ("DealParty".equals(entityName)) {
                        fieldValue = getFieldValueFromList(dealPartiesList, getterMethodName);
                    } else if ("Settlement".equals(entityName)) {
                        fieldValue = getFieldValueFromList(settlementList, getterMethodName);
                    }
                } catch (Exception e) {
                    // Log error if there's an issue accessing the field
                    logger.error("Error accessing field " + fieldName + " in entity " + entityName, e);
                }

                // Write the field value to the writer if it's not null
                if (fieldValue != null) {
                    writer.write(mtTag + ":" + fieldValue + "\r\n");
                }
            }
        }
    }

    private void processMappingRule(BufferedWriter writer, String mappingRule, String mtTag, InfoDeal infoDeal, List<DealGoods> dealGoodsList, List<DealParty> dealPartiesList, List<Settlement> settlementList) throws IOException {
        try {
            // Parse the mapping rule JSON
            JSONObject ruleJson = new JSONObject(mappingRule);
            JSONArray fieldsArray = ruleJson.optJSONArray("fields");
            String delimiter = ruleJson.optString("delimiter", "");
            StringBuilder combinedValue = new StringBuilder();

            if (fieldsArray != null) {
                for (int i = 0; i < fieldsArray.length(); i++) {
                    // Split the full field name into entity and field parts
                    String fullFieldName = fieldsArray.getString(i);
                    String[] parts = fullFieldName.split("\\.");
                    String entity = parts[0];
                    String field = parts[1];
                    String getterMethodName = "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);

                    Object fieldValue = null;
                    try {
                        // Retrieve the field value based on the entity name
                        if ("InfoDeal".equals(entity)) {
                            fieldValue = infoDeal.getClass().getMethod(getterMethodName).invoke(infoDeal);
                        } else if ("DealGoods".equals(entity)) {
                            fieldValue = getFieldValueFromList(dealGoodsList, getterMethodName);
                        } else if ("DealParty".equals(entity)) {
                            fieldValue = getFieldValueFromList(dealPartiesList, getterMethodName);
                        } else if ("Settlement".equals(entity)) {
                            fieldValue = getFieldValueFromList(settlementList, getterMethodName);
                        }
                    } catch (Exception e) {
                        // Log error if there's an issue accessing the field
                        logger.error("Error accessing field " + field + " in entity " + entity, e);
                    }

                    // Append the field value to the combined value if it's not null
                    if (fieldValue != null) {
                        if (!combinedValue.isEmpty()) {
                            combinedValue.append(delimiter);
                        }
                        combinedValue.append(fieldValue.toString());
                    }
                }

                // Write the combined value to the writer if it's not empty
                if (!combinedValue.isEmpty()) {
                    writer.write(mtTag + ":" + combinedValue.toString() + "\r\n");
                }
            }
        } catch (JSONException e) {
            // Log error if there's an issue parsing the mapping rule
            logger.error("Error parsing mappingRule: " + mappingRule, e);
        }
    }

    private Object getFieldValueFromList(List<?> list, String getterMethodName) {
        for (Object obj : list) {
            try {
                // Invoke the getter method to retrieve the field value
                Object value = obj.getClass().getMethod(getterMethodName).invoke(obj);
                if (value != null) {
                    return value;
                }
            } catch (Exception e) {
                // Log error if there's an issue accessing the field in list element
                logger.error("Error accessing field using method " + getterMethodName + " in list element", e);
            }
        }
        return null; // Return null if no value is found
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

    public List<DealParty> parties() { return partiesRepo.findAll();}

    public DealParty dealPartyById(Integer id){
        Optional<DealParty> party = partiesRepo.findById(id);
        return party.orElse(null);
    }

    public List<DealParty> partiesByDealId(Integer id) {
        return partiesRepo.findPartiesByDealId(id);
    }


    ////////////////////Settlement////////////////////
    /////////////////////////////////////////////////

    public List<Settlement> settlements() { return settRepo.findAll();}

    public Settlement settlementById(Integer id){
        Optional<Settlement> settlement = settRepo.findById(id);
        return settlement.orElse(null);
    }

    public List<Settlement> settlementsByDealId(Integer id) {
        return settRepo.findSettlementsByDealId(id);
    }


}
