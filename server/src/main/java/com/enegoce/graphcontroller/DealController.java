package com.enegoce.graphcontroller;

import com.enegoce.entities.*;
import com.enegoce.service.DealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class DealController {

    @Autowired
    private DealService dealService;

    @QueryMapping
    public List<InfoDeal> getAllInfoDeals() {
        return dealService.deals();
    }

    @QueryMapping
    public InfoDeal getInfoDealById(@Argument Long id) {
        return dealService.dealById(id);
    }

    /////////////////////DealGoods//////////////////////
    ////////////////////////////////////////////////////

    @QueryMapping
    public List<DealGoods> goodsByDealId(@Argument Long id) {
        return dealService.goodsByDealId(id);
    }

    ////////////////////DealParty////////////////////
    /////////////////////////////////////////////////

    @QueryMapping
    public List<DealParty> partiesByDealId(@Argument Long id) {
        return dealService.partiesByDealId(id);
    }

    @QueryMapping
    public DealParty partyByDealIdAndCode(@Argument Long id, @Argument String code) {
        return dealService.partyByDealIdAndCode(id, code);
    }

    ////////////////////Settlement///////////////////
    /////////////////////////////////////////////////

    @QueryMapping
    public Settlment settlementById(@Argument Long id) {
        return dealService.settlementById(id);
    }


}
