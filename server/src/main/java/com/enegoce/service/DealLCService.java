package com.enegoce.service;

import com.enegoce.entities.DealLC;
import com.enegoce.entities.DealLCInput;
import com.enegoce.repository.DealLCRepository;
import jakarta.validation.Valid;
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
import java.util.List;
import java.util.Optional;

@Service
public class DealLCService {

    @Autowired
    private DealLCRepository dealRepo;

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
