package com.enegoce.graphcontroller;

import com.enegoce.entities.DealLC;
import com.enegoce.entities.DealLCInput;
import com.enegoce.service.DealLCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Controller
public class DealLCController {

    private static final Logger logger = LogManager.getLogger(DealLCController.class);

    @Autowired
    private DealLCService service;

    @QueryMapping
    public List<DealLC> getAllDealLCs() {
        return service.deals();
    }

    @MutationMapping
    public DealLC addDealLC(@Argument DealLCInput input) {
        return service.createDealLC(input);
    }

    @QueryMapping
    public DealLC getDealLCById(@Argument Integer id) {
        return service.dealById(id);
    }

    @MutationMapping
    public String exportDeal(@Argument Integer id) {
        DealLC deal = service.dealById(id);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String finFilePath = "C:/Users/Assil/IdeaProjects/enegoce/server/src/test/output/Fin_700_" + timestamp + ".txt";

        boolean conversionSuccessful = service.exportFIN700(deal, finFilePath);
        String response;

        if (conversionSuccessful) {
            response = String.valueOf(ResponseEntity.ok()
                    .body("{\"message\": \"Conversion successful\", \"finFilePath\": \"" + finFilePath + "\"}"));
        } else {
            logger.error("Unsuccessful Conversion");
            response = String.valueOf(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Conversion failed. Please check your Input and try again.\"}"));
        }
        return response;

    }

}
