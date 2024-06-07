package com.enegoce.service;

import com.enegoce.entities.DealGoods;
import com.enegoce.entities.InfoDeal;
import com.enegoce.entities.InfoDealInput;
import com.enegoce.entities.MtFieldMapping;
import com.enegoce.repository.DealGoodsRepository;
import com.enegoce.repository.InfoDealRepository;
import com.enegoce.repository.MtFieldMappingRepository;
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
import java.util.ArrayList;
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

    /*public boolean generateAndExportMtMessage(Integer dealId, String mt, String filePath) {
        List<MtFieldMapping> mappings = mappingRepo.findByMt(mt);
        if (mappings.isEmpty()) {
            logger.error("No mappings found for mt: " + mt);
            return false;
        }

        mappings.sort(Comparator.comparingInt(MtFieldMapping::getFieldOrder));
        logger.info(mappings);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            return generateAndExportMtMessageWithWriter(dealId, mt, writer, mappings);
        } catch (Exception e) {
            logger.error("Error generating MT message: " + e);
            return false;
        }
    }*/

    /*private boolean generateAndExportMtMessageWithWriter(Integer dealId, String mt, BufferedWriter writer, List<MtFieldMapping> mappings) {
        InfoDeal infoDeal = null;
        List<DealGoods> dealGoodsList = new ArrayList<>();

        if ("700".equals(mt)) {
            infoDeal = this.dealById(dealId);
            if (infoDeal == null) {
                logger.error("Entity not found for id: " + dealId);
                return false;
            }
        } else if ("701".equals(mt)) {
            dealGoodsList = this.goodsByDealId(dealId);
            if (dealGoodsList == null || dealGoodsList.isEmpty()) {
                logger.error("Goods not found for deal id: " + dealId);
                return false;
            }
        }

        try {
            if ("700".equals(mt)) {
                processInfoDeal(writer, infoDeal, mappings);
            } else if ("701".equals(mt)) {
                processDealGoods(writer, dealGoodsList, mappings);
            }
            return true; // Return true if writing is successful
        } catch (Exception e) {
            logger.error("Error processing data: " + e);
            return false; // Return false if processing data fails
        }
    }*/

    /*private void processInfoDeal(BufferedWriter writer, InfoDeal infoDeal, List<MtFieldMapping> mappings) throws IOException {
        // Iterate through each mapping
        for (MtFieldMapping mapping : mappings) {
            String entityName = mapping.getEntityName();
            String fieldName = mapping.getDatabaseField();
            String mtTag = mapping.getTag();
            String mappingRule = mapping.getMappingRule();

            // Check if there's a mapping rule
            if (mappingRule != null && !mappingRule.isEmpty()) {
                // Debug log for mappingRule
                logger.debug("Processing mappingRule: " + mappingRule);

                // Parse the mapping rule
                try {
                    JSONObject ruleJson = new JSONObject(mappingRule);
                    JSONArray fieldsArray = ruleJson.optJSONArray("fields");
                    String delimiter = ruleJson.optString("delimiter", "");
                    StringBuilder combinedValue = new StringBuilder();

                    // Check if fields array exists
                    if (fieldsArray != null) {
                        // Iterate through each field in the mapping rule
                        for (int i = 0; i < fieldsArray.length(); i++) {
                            String fullFieldName = fieldsArray.getString(i);
                            String[] parts = fullFieldName.split("\\.");
                            String entity = parts[0];
                            String field = parts[1];
                            String getterMethodName = "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);

                            Object fieldValue = null;
                            try {
                                // Get field value using reflection
                                if ("InfoDeal".equals(entity)) {
                                    fieldValue = infoDeal.getClass().getMethod(getterMethodName).invoke(infoDeal);
                                } else if ("DealGoods".equals(entity)) {
                                    // Handle DealGoods if necessary
                                }
                            } catch (Exception e) {
                                // Log error if accessing field fails
                                logger.error("Error accessing field " + field + " in entity " + entity, e);
                            }

                            // Append field value to combinedValue if not null
                            if (fieldValue != null) {
                                if (!combinedValue.isEmpty()) {
                                    combinedValue.append(delimiter);
                                }
                                combinedValue.append(fieldValue.toString());
                            }
                        }

                        // Write combined value to writer
                        if (!combinedValue.isEmpty()) {
                            writer.write(mtTag + ":" + combinedValue.toString() + "\r\n");
                        }
                    }
                } catch (JSONException e) {
                    // Log error if parsing mapping rule fails
                    logger.error("Error parsing mappingRule: " + mappingRule, e);
                }
            } else {
                // Handle normal single field logic

                // Skip if fieldName or entityName is null
                if (fieldName == null || entityName == null) {
                    continue;
                }

                // Skip if fieldName or entityName contains placeholder
                if (fieldName.contains("//todo//") || entityName.contains("//todo//")) { //TODO: Temporary
                    continue;
                }

                // Construct getter method name
                String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                Object fieldValue = null;
                try {
                    // Get field value using reflection
                    if ("InfoDeal".equals(entityName)) {
                        fieldValue = infoDeal.getClass().getMethod(getterMethodName).invoke(infoDeal);
                    }
                } catch (Exception e) {
                    // Log error if accessing field fails
                    logger.error("Error accessing field " + fieldName + " in entity " + entityName, e);
                }

                // Write field value to writer if not null
                if (fieldValue != null) {
                    writer.write(mtTag + ":" + fieldValue + "\r\n");
                }
            }
        }
    }*/

    /*private void processDealGoods(BufferedWriter writer, List<DealGoods> dealGoodsList, List<MtFieldMapping> mappings) throws IOException {
        int totalGoods = dealGoodsList.size();
        int counter = 1;

        for (DealGoods dealGoods : dealGoodsList) {
            // Write sequence total for each entry
            writer.write("27:" + counter + "/" + totalGoods + "\r\n");

            for (MtFieldMapping mapping : mappings) {
                String entityName = mapping.getEntityName();
                String fieldName = mapping.getDatabaseField();
                String mtTag = mapping.getTag();

                // Skip if fieldName or entityName is null
                if (fieldName == null || entityName == null) {
                    continue;
                }

                Object fieldValue = null;
                try {
                    if ("DealGoods".equals(entityName)) {
                        if ("InfoDeal".equals(fieldName)) {
                            fieldValue = dealGoods.getDeal().getId();
                        } else {
                            // Handle other fields of DealGoods entity
                            String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                            fieldValue = dealGoods.getClass().getMethod(getterMethodName).invoke(dealGoods);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error accessing field " + fieldName + " in entity " + entityName, e);
                }

                if (fieldValue != null) {
                    writer.write(mtTag + ":" + fieldValue + "\r\n");
                }
            }
            counter++;
        }
    }*/

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

    public boolean generateAndExportMtMessage(Integer dealId, String mt, String filePath) {
        List<MtFieldMapping> mappings = mappingRepo.findByMt(mt);
        if (mappings.isEmpty()) {
            logger.error("No mappings found for mt: " + mt);
            return false;
        }

        mappings.sort(Comparator.comparingInt(MtFieldMapping::getFieldOrder));
        logger.info(mappings);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            InfoDeal infoDeal = this.dealById(dealId);
            return generateAndExportMtMessageWithWriter(infoDeal, writer, mappings);
        } catch (Exception e) {
            logger.error("Error generating MT message: " + e);
            return false;
        }
    }

    private boolean generateAndExportMtMessageWithWriter(InfoDeal infoDeal, BufferedWriter writer, List<MtFieldMapping> mappings) {
        try {
            processInfoDeal(writer, infoDeal, mappings);
            return true; // Return true if writing is successful
        } catch (Exception e) {
            logger.error("Error processing data: " + e);
            return false; // Return false if processing data fails
        }
    }

    private void processInfoDeal(BufferedWriter writer, InfoDeal infoDeal, List<MtFieldMapping> mappings) throws IOException {
        // Iterate through each mapping
        for (MtFieldMapping mapping : mappings) {
            String entityName = mapping.getEntityName();
            String fieldName = mapping.getDatabaseField();
            String mtTag = mapping.getTag();
            String mappingRule = mapping.getMappingRule();

            // Check if there's a mapping rule
            if (mappingRule != null && !mappingRule.isEmpty()) {
                // Debug log for mappingRule
                logger.debug("Processing mappingRule: " + mappingRule);

                // Parse the mapping rule
                try {
                    JSONObject ruleJson = new JSONObject(mappingRule);
                    JSONArray fieldsArray = ruleJson.optJSONArray("fields");
                    String delimiter = ruleJson.optString("delimiter", "");
                    StringBuilder combinedValue = new StringBuilder();

                    // Check if fields array exists
                    if (fieldsArray != null) {
                        // Iterate through each field in the mapping rule
                        for (int i = 0; i < fieldsArray.length(); i++) {
                            String fullFieldName = fieldsArray.getString(i);
                            String[] parts = fullFieldName.split("\\.");
                            String entity = parts[0];
                            String field = parts[1];
                            String getterMethodName = "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);

                            Object fieldValue = null;
                            try {
                                // Get field value using reflection
                                if ("InfoDeal".equals(entity)) {
                                    fieldValue = infoDeal.getClass().getMethod(getterMethodName).invoke(infoDeal);
                                }
                            } catch (Exception e) {
                                // Log error if accessing field fails
                                logger.error("Error accessing field " + field + " in entity " + entity, e);
                            }

                            // Append field value to combinedValue if not null
                            if (fieldValue != null) {
                                if (!combinedValue.isEmpty()) {
                                    combinedValue.append(delimiter);
                                }
                                combinedValue.append(fieldValue.toString());
                            }
                        }

                        // Write combined value to writer
                        if (!combinedValue.isEmpty()) {
                            writer.write(mtTag + ":" + combinedValue.toString() + "\r\n");
                        }
                    }
                } catch (JSONException e) {
                    // Log error if parsing mapping rule fails
                    logger.error("Error parsing mappingRule: " + mappingRule, e);
                }
            } else {
                // Handle normal single field logic

                // Skip if fieldName or entityName is null
                if (fieldName == null || entityName == null) {
                    continue;
                }

                // Skip if fieldName or entityName contains placeholder
                if (fieldName.contains("//todo//") || entityName.contains("//todo//")) { //TODO: Temporary
                    continue;
                }

                // Construct getter method name
                String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                Object fieldValue = null;
                try {
                    // Get field value using reflection
                    if ("InfoDeal".equals(entityName)) {
                        fieldValue = infoDeal.getClass().getMethod(getterMethodName).invoke(infoDeal);
                    }
                } catch (Exception e) {
                    // Log error if accessing field fails
                    logger.error("Error accessing field " + fieldName + " in entity " + entityName, e);
                }

                // Write field value to writer if not null
                if (fieldValue != null) {
                    writer.write(mtTag + ":" + fieldValue + "\r\n");
                }
            }
        }
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
}
