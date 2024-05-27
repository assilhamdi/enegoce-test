package com.enegoce.service;

import com.enegoce.entities.DealGoods;
import com.enegoce.entities.DealLC;
import com.enegoce.entities.DealLCInput;
import com.enegoce.entities.MtFieldMapping;
import com.enegoce.repository.DealGoodsRepository;
import com.enegoce.repository.DealLCRepository;
import com.enegoce.repository.MtFieldMappingRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private DealLCRepository dealRepo;

    @Autowired
    private MtFieldMappingRepository mappingRepo;

    @Autowired
    private DealGoodsRepository goodsRepo;

    private static final Logger logger = LogManager.getLogger(DealService.class);

    public List<DealLC> deals() {
        return dealRepo.findAll();
    }

    public DealLC dealById(Integer id) {
        Optional<DealLC> deal = dealRepo.findById(id);
        return deal.orElse(null);
    }

    public DealLC createDealLC(DealLCInput dealInput) {
        DealLC deal = new DealLC();

        deal.setFormLC(dealInput.formLC());
        deal.setCustomerReference(dealInput.customerReference());
        deal.setCounterParty(dealInput.counterParty());
        deal.setBankISSRef(dealInput.bankISSRef());
        deal.setBankRMBRef(dealInput.bankRMBRef());
        deal.setCurrencyId(dealInput.currencyId());
        deal.setAvailableWith(dealInput.availableWith());
        deal.setPartialTranshipment(dealInput.partialTranshipment());
        deal.setTranshipment(dealInput.transhipment());

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

    public boolean generateAndExportMtMessage(Integer dealId, String mt, String filePath) {
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
    }

    private boolean generateAndExportMtMessageWithWriter(Integer dealId, String mt, BufferedWriter writer, List<MtFieldMapping> mappings) {
        DealLC dealLC = null;
        List<DealGoods> dealGoodsList = new ArrayList<>();

        if ("700".equals(mt)) {
            dealLC = this.dealById(dealId);
            if (dealLC == null) {
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
                processDealLC(writer, dealLC, mappings);
            } else if ("701".equals(mt)) {
                processDealGoods(writer, dealGoodsList, mappings);
            }
            return true; // Return true if writing is successful
        } catch (Exception e) {
            logger.error("Error processing data: " + e);
            return false; // Return false if processing data fails
        }
    }


    public boolean generateAndExportMt798Message(Integer dealId, String mt, String filePath) {
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
    }


    private void processDealLC(BufferedWriter writer, DealLC dealLC, List<MtFieldMapping> mappings) throws IOException {
        for (MtFieldMapping mapping : mappings) {
            String entityName = mapping.getEntityName();
            String fieldName = mapping.getDatabaseField();
            String mtTag = mapping.getTag();

            // Skip if fieldName or entityName is null
            if (fieldName == null || entityName == null) {
                continue;
            }

            // Find corresponding getter method for the field
            String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            logger.info(getterMethodName);
            Object fieldValue = null;
            try {
                if ("DealLC".equals(entityName)) {
                    fieldValue = dealLC.getClass().getMethod(getterMethodName).invoke(dealLC);
                }
            } catch (Exception e) {
                logger.error("Error accessing field " + fieldName + " in entity " + entityName, e);
            }

            if (fieldValue != null) {
                writer.write(mtTag + ":" + fieldValue + "\r\n");
            }
        }
    }

    private void processDealGoods(BufferedWriter writer, List<DealGoods> dealGoodsList, List<MtFieldMapping> mappings) throws IOException {
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
                        if ("dealLC".equals(fieldName)) {
                            fieldValue = dealGoods.getDealLC().getDealId();
                        } else {
                            // Handle other fields of DealGoods entity
                            String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                            fieldValue = dealGoods.getClass().getMethod(getterMethodName).invoke(dealGoods);
                        }
                    } /*else if ("DealLC".equals(entityName)) {
                        // Handle fields of associated DealLC entity
                        DealLC dealLC = dealGoods.getDealLC();
                        String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                        fieldValue = dealLC.getClass().getMethod(getterMethodName).invoke(dealLC);
                    }*/
                } catch (Exception e) {
                    logger.error("Error accessing field " + fieldName + " in entity " + entityName, e);
                }

                if (fieldValue != null) {
                    writer.write(mtTag + ":" + fieldValue + "\r\n");
                }
            }
            counter++;
        }
    }


    public boolean exportFIN700(DealLC deal, String finFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(finFilePath))) {
            // **Message Header**
            writer.write("------------------------- Message Header -------------------------");
            writer.newLine();
            writer.write("Swift Input: FIN 700  Issue of a Documentary Credit");
            writer.newLine();
            writer.write("Sender  : " + deal.getBankISSRef());
            writer.newLine();
            writer.write("Receiver: " + deal.getBankRMBRef());
            writer.newLine();

            writer.write("-------------------------- Message Text -------------------------");
            writer.newLine();
            writer.write("27 : Sequence of Total");
            writer.newLine();
            writer.write("     N/A"); //TODO 27:
            writer.newLine();

            // **Essential Message Fields**
            writer.write("40A: Form of Documentary Credit");
            writer.newLine();
            writer.write("     " + deal.getFormLC());
            writer.newLine(); // Adding a new line after each field for readability
            writer.write("20 : Documentary Credit Number");
            writer.newLine();
            writer.write("     " + deal.getDealId());
            writer.newLine();
            writer.write("31C: Date of Issue " + deal.getDueDate());
            writer.newLine();
            writer.write("40E: Applicable Rules");
            writer.newLine();
            writer.write("     " + "N/A"); //TODO 40E:
            writer.newLine();
            writer.write("31D: Date and Place of Expiry " + deal.getExpiryDate() + " " + deal.getExpiryPlace());
            writer.newLine();

            // **Additional Fields (based on FIN 700 structure)**
            writer.write("50 : Applicant");
            writer.newLine();
            writer.write("     " + deal.getCustomerReference());
            writer.newLine();
            writer.write("59 : Beneficiary - Name & Address");
            writer.newLine();
            writer.write("     " + deal.getCounterParty());
            writer.newLine();
            writer.write("32B: Currency Code, Amount");
            writer.newLine();
            writer.write("     Currency: " + deal.getCurrencyId());
            writer.newLine();
            writer.write("     Amount: " + deal.getLcAmount());
            writer.newLine();
            writer.write("39A: Percentage Credit Amt Tolerance");
            writer.newLine();
            writer.write("     " + deal.getVarAmountTolerance());
            writer.newLine();

            // **Optional Fields (if applicable)**
            writer.write("41A: Available With");
            writer.newLine();
            writer.write("     " + deal.getAvailableWith());
            writer.newLine();
            writer.write("43P: Partial Shipments");
            writer.newLine();
            writer.write("     " + deal.getPartialTranshipment());
            writer.newLine();
            writer.write("43T: Transhipment");
            writer.newLine();
            writer.write("     " + deal.getTranshipment());
            writer.newLine();
            //TODO : 44F

        } catch (IOException e) {
            logger.error("An Error has occurred", e);
            return false;
        }


        return true;
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
