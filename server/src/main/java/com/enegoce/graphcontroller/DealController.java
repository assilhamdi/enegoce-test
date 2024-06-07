package com.enegoce.graphcontroller;

import com.enegoce.entities.*;
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
    public List<InfoDeal> getAllInfoDeals() {
        return service.deals();
    }

    @MutationMapping
    public InfoDeal addInfoDeal(@Argument InfoDealInput input) {
        return service.createInfoDeal(input);
    }

    @QueryMapping
    public InfoDeal getInfoDealById(@Argument Integer id) {
        return service.dealById(id);
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

    /*@MutationMapping
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
    }*/


    //////////////////////////////DealGoods//////////////////////////////
    /////////////////////////////////////////////////////////////////////

    @QueryMapping
    public List<DealGoods> getAllDealGoods() {
        return service.goods();
    }

    @QueryMapping
    public DealGoods dealGoodsById(@Argument Integer id) {
        return service.dealGoodsById(id);
    }

    @QueryMapping
    public List<DealGoods> goodsByDealId(@Argument Integer id) {
        return service.goodsByDealId(id);
    }

    ////////////////////DealParty////////////////////
    /////////////////////////////////////////////////

    @QueryMapping
    public List<DealParty> getAllDealParties() {return service.parties();}

    @QueryMapping
    public DealParty dealPartyById(@Argument Integer id) {return service.dealPartyById(id);}

    @QueryMapping
    public List<DealParty> partiesByDealId(@Argument Integer id) {return service.partiesByDealId(id);}

    ////////////////////Settlement////////////////////
    /////////////////////////////////////////////////

    @QueryMapping
    public List<Settlement> getAllSettlements() {return service.settlements();}

    @QueryMapping
    public Settlement settlementById (@Argument Integer id) {return service.settlementById(id);}

    @QueryMapping
    public List<Settlement> settlementsByDealId (@Argument Integer id){ return service.settlementsByDealId(id);}


}
