package com.enegoce.service;

import com.enegoce.entities.*;
import com.enegoce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DealService {

    @Autowired
    private InfoDealRepository dealRepo;

    @Autowired
    private DealGoodsRepository goodsRepo;

    @Autowired
    private DealPartyRepository partiesRepo;

    @Autowired
    private DealCommentRepository commRepo;

    @Autowired
    private SettlementRepository settRepo;


    ////////////////////InfoDeal/////////////////////
    /////////////////////////////////////////////////00.

    public List<InfoDeal> deals() {
        return dealRepo.findAll();
    }

    public InfoDeal dealById(Integer id) {
        Optional<InfoDeal> deal = dealRepo.findById(id);
        return deal.orElse(null);
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

    ////////////////////Comment//////////////////////
    /////////////////////////////////////////////////

    public List<DealComment> comments() {
        return commRepo.findAll();
    }

    public DealComment commentById(Integer id) {
        Optional<DealComment> comment = commRepo.findById(id);
        return comment.orElse(null);
    }

    public List<DealComment> commentsByDealId(Integer id) {
        return commRepo.findCommentsByDealId(id);
    }

    public DealComment commentByDealAndType(Integer id, String type) {
        return commRepo.findCommentByDealAndType(id, type);
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
