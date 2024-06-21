package com.enegoce.graphcontroller;

import com.enegoce.entities.*;
import com.enegoce.service.DealService;
import com.enegoce.service.MTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Controller
public class DealController {

    @Autowired
    private DealService dealService;

    @QueryMapping
    public List<InfoDeal> getAllInfoDeals() {
        return dealService.deals();
    }

    @QueryMapping
    public InfoDeal getInfoDealById(@Argument Integer id) {
        return dealService.dealById(id);
    }



    //////////////////////////////DealGoods//////////////////////////////
    /////////////////////////////////////////////////////////////////////

    @QueryMapping
    public List<DealGoods> getAllDealGoods() {
        return dealService.goods();
    }

    @QueryMapping
    public DealGoods dealGoodsById(@Argument Integer id) {
        return dealService.dealGoodsById(id);
    }

    @QueryMapping
    public List<DealGoods> goodsByDealId(@Argument Integer id) {
        return dealService.goodsByDealId(id);
    }

    ////////////////////DealParty////////////////////
    /////////////////////////////////////////////////

    @QueryMapping
    public List<DealParty> getAllDealParties() {
        return dealService.parties();
    }

    @QueryMapping
    public DealParty dealPartyById(@Argument Integer id) {
        return dealService.dealPartyById(id);
    }

    @QueryMapping
    public List<DealParty> partiesByDealId(@Argument Integer id) {
        return dealService.partiesByDealId(id);
    }

    @QueryMapping
    public DealParty partyByDealIdAndCode(@Argument Integer id, @Argument String code) {
        return dealService.partyByDealIdAndCode(id, code);
    }

    ////////////////////Settlement////////////////////
    /////////////////////////////////////////////////

    @QueryMapping
    public List<Settlement> getAllSettlements() {
        return dealService.settlements();
    }

    @QueryMapping
    public Settlement settlementById(@Argument Integer id) {
        return dealService.settlementById(id);
    }

    @QueryMapping
    public List<Settlement> settlementsByDealId(@Argument Integer id) {
        return dealService.settlementsByDealId(id);
    }

}
