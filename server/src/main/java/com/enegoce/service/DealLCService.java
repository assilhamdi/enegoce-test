package com.enegoce.service;

import com.enegoce.entities.DealLC;
import com.enegoce.entities.DealLCInput;
import com.enegoce.entities.MtFieldMapping;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class DealLCService {

    @Autowired
    private DealLCRepository dealRepo;

    @Autowired
    private MtFieldMappingRepository mappingRepo;

    private static final Logger logger = LogManager.getLogger(DealLCService.class);

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


        Object entity;
        List<MtFieldMapping> mappings;

        if ("700".equals(mt)) {
            entity = this.dealById(dealId);
            if (entity == null) {
                logger.error("Entity not found for id: " + dealId);
                return false;
            }
            mappings = mappingRepo.findByMt(mt);
            if (mappings.isEmpty()) {
                logger.error("No mappings found for mt: " + mt);
                return false;
            }
        } /*else if ("701".equals(mt)) {
            entity = dealGoodsService.dealGoodsById(dealId); //TODO: dealGoodsService
        }*/ else {
            logger.error("Unsupported MT: " + mt);
            return false;
        }

        mappings.sort(Comparator.comparingInt(MtFieldMapping::getFieldOrder));
        logger.info(mappings);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (MtFieldMapping mapping : mappings) {
                String entityName = mapping.getEntityName();
                String fieldName = mapping.getDatabaseField();
                String mt700Tag = mapping.getTag();

                // Find corresponding getter method for the field
                String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                logger.info(getterMethodName);
                Object fieldValue = null;
                try {
                    // Dynamically invoke getter method based on entity name
                    if ("DealLC".equals(entityName)) {
                        fieldValue = entity.getClass().getMethod(getterMethodName).invoke(entity);
                    } /*else if ("OtherEntity".equals(entityName)) {
                        // Fetch field value from other entity based on entity name
                    }*/
                } catch (Exception e) {
                    // Handle exception if getter method not found or other issues
                    logger.error("Error accessing field " + fieldName + " in entity " + entityName, e);
                }

                if (fieldValue != null) {
                    // Write MT700 message to text file
                    writer.write(mt700Tag + ":" + fieldValue + "\r\n");
                }
            }
            return true; // Return true if writing is successful
        } catch (Exception e) {
            logger.error("Error accessing file or database: " + e);
            return false; // Return false if accessing file or database fails
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
}
