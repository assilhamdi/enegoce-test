package com.enegoce.graphcontroller;

import com.enegoce.entities.DealGoods;
import com.enegoce.entities.DealLC;
import com.enegoce.entities.DealLCInput;
import com.enegoce.service.DealService;
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
public class DealController {

    private static final Logger logger = LogManager.getLogger(DealController.class);

    @Autowired
    private DealService service;

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

    @MutationMapping
    public String exportMT(@Argument Integer id, @Argument String mt) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String mtFilePath = "C:/Users/Assil/IdeaProjects/enegoce/server/src/test/output/MT" + mt + "_" + timestamp + ".txt";

        boolean conversionSuccessful = service.generateAndExportMtMessage(id, mt, mtFilePath);
        String response;

        if (conversionSuccessful) {
            response = String.format("{\"message\": \"Conversion successful\", \"filePath\": \"%s\"}", mtFilePath);
            return ResponseEntity.ok(response).getBody();
        } else {
            logger.error("Unsuccessful Conversion");
            response = "{\"message\": \"Conversion failed. Please check your Input and try again.\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response).getBody();
        }
    }

    @MutationMapping
    public String exportMT798(@Argument Integer dealId, @Argument String mt) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String mtFilePath = "C:/Users/Assil/IdeaProjects/enegoce/server/src/test/output/MT798_" + timestamp + ".txt";

        boolean conversionSuccessful = service.generateAndExportMt798Message(dealId, mt, mtFilePath);
        String response;

        if (conversionSuccessful) {
            response = String.format("{\"message\": \"Conversion successful\", \"filePath\": \"%s\"}", mtFilePath);
            return ResponseEntity.ok(response).getBody();
        } else {
            logger.error("Unsuccessful Conversion");
            response = "{\"message\": \"Conversion failed. Please check your Input and try again.\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response).getBody();
        }
    }



    //////////////////////////////DealGoods//////////////////////////////
    /////////////////////////////////////////////////////////////////////

    @QueryMapping
    public List<DealGoods> getAllDealGoods() {return service.goods();}

    @QueryMapping
    public DealGoods dealGoodsById(@Argument Integer id) {return service.dealGoodsById(id);}

    @QueryMapping
    public List<DealGoods> goodsByDealId(@Argument Integer id) {return service.goodsByDealId(id);}



}
