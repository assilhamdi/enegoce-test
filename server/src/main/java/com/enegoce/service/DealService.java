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
import java.util.*;

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

            return generateAndExportMtMessageWithWriter(infoDeal, dealGoodsList, dealPartiesList, settlementList, writer, mappings);
        } catch (Exception e) {
            logger.error("Error generating MT message: " + e);
            return false;
        }
    }

    private boolean generateAndExportMtMessageWithWriter(InfoDeal infoDeal, List<DealGoods> dealGoodsList, List<DealParty> dealPartiesList, List<Settlement> settlementList, BufferedWriter writer, List<MtFieldMapping> mappings) {
        try {
            processInfoDeal(writer, infoDeal, dealGoodsList, dealPartiesList, settlementList, mappings);
            return true;
        } catch (Exception e) {
            logger.error("Error processing data: " + e);
            return false;
        }
    }

    private void processInfoDeal(BufferedWriter writer, InfoDeal infoDeal, List<DealGoods> dealGoodsList, List<DealParty> dealPartiesList, List<Settlement> settlementList, List<MtFieldMapping> mappings) throws IOException {
        for (MtFieldMapping mapping : mappings) {
            String entityName = mapping.getEntityName();
            String fieldName = mapping.getDatabaseField();
            String mtTag = mapping.getTag();
            String mappingRule = mapping.getMappingRule();

            if (mappingRule != null && !mappingRule.isEmpty()) {
                processMappingRule(writer, mappingRule, mtTag, infoDeal, dealGoodsList, dealPartiesList, settlementList);
            } else {
                if (fieldName == null || entityName == null || fieldName.contains("//todo//") || entityName.contains("//todo//")) {
                    continue;
                }

                String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                Object fieldValue = null;

                try {
                    fieldValue = getFieldValue(entityName, getterMethodName, infoDeal, dealGoodsList, dealPartiesList, settlementList, null);
                } catch (Exception e) {
                    logger.error("Error accessing field " + fieldName + " in entity " + entityName, e);
                }

                if (fieldValue != null) {
                    writer.write(mtTag + ":" + fieldValue + "\r\n");
                }
            }
        }
    }

    private void processMappingRule(BufferedWriter writer, String mappingRule, String mtTag, InfoDeal infoDeal, List<DealGoods> dealGoodsList, List<DealParty> dealPartiesList, List<Settlement> settlementList) throws IOException {
        try {
            JSONObject ruleJson = new JSONObject(mappingRule);
            JSONArray fieldsArray = ruleJson.optJSONArray("fields");
            String delimiter = ruleJson.optString("delimiter", "");
            String code = ruleJson.optString("code", null);

            if (fieldsArray != null) {
                StringBuilder combinedValue = new StringBuilder();
                DealParty party = code != null ? partyByDealIdAndCode(infoDeal.getId(), code) : null;

                if (party == null && code != null) {
                    logger.warn("No DealParty found for code: " + code);
                    return;
                }

                for (int i = 0; i < fieldsArray.length(); i++) {
                    String[] parts = fieldsArray.getString(i).split("\\.");
                    String entity = parts[0];
                    String field = parts[1];
                    String getterMethodName = "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);

                    Object fieldValue = getFieldValue(entity, getterMethodName, infoDeal, dealGoodsList, dealPartiesList, settlementList, party);

                    if (fieldValue != null) {
                        if (!combinedValue.isEmpty()) {
                            combinedValue.append(delimiter);
                        }
                        combinedValue.append(fieldValue.toString());
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

    private Object getFieldValue(String entityName, String getterMethodName, InfoDeal infoDeal, List<DealGoods> dealGoodsList, List<DealParty> dealPartiesList, List<Settlement> settlementList, DealParty party) throws Exception {
        switch (entityName) {
            case "InfoDeal":
                return infoDeal.getClass().getMethod(getterMethodName).invoke(infoDeal);
            case "DealGoods":
                return getFieldValueFromList(dealGoodsList, getterMethodName);
            case "Settlement":
                return getFieldValueFromList(settlementList, getterMethodName);
            case "DealParty":
                return party != null ? party.getClass().getMethod(getterMethodName).invoke(party) : getFieldValueFromList(dealPartiesList, getterMethodName);
            default:
                return null;
        }
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
