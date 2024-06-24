package com.enegoce.service;

import com.enegoce.entities.*;
import com.enegoce.repository.*;
import com.engoce.deal.dto.InfoDealDto;
import com.engoce.deal.dto.SettlementDto;
import jakarta.persistence.EntityNotFoundException;
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

    public InfoDeal dealById(Long id) {
        Optional<InfoDeal> deal = dealRepo.findById(id);
        return deal.orElse(null);
    }
    public InfoDeal saveInfoDeal(InfoDealDto infoDealDto) {
        InfoDeal infoDeal = mapDtoToEntity(infoDealDto);
        return dealRepo.save(infoDeal);
    }

    public InfoDeal updateInfoDeal(Long id, InfoDealDto infoDealDto) {
        Optional<InfoDeal> optionalInfoDeal = dealRepo.findById(id);
        if (optionalInfoDeal.isPresent()) {
            InfoDeal infoDeal = optionalInfoDeal.get();
            infoDeal = updateEntityFromDto(infoDeal, infoDealDto);
            return dealRepo.save(infoDeal);
        } else {
            throw new EntityNotFoundException("Deal with id " + id + " not found");
        }
    }


    ////////////////////DealGoods////////////////////
    /////////////////////////////////////////////////

    public List<DealGoods> goods() {
        return goodsRepo.findAll();
    }

    public DealGoods dealGoodsById(Long id) {
        Optional<DealGoods> deal = goodsRepo.findById(id);
        return deal.orElse(null);
    }

    public List<DealGoods> goodsByDealId(Long id) {
        return goodsRepo.findGoodsByDealId(id);
    }


    ////////////////////DealParty////////////////////
    /////////////////////////////////////////////////

    public List<DealParty> parties() {
        return partiesRepo.findAll();
    }

    public DealParty dealPartyById(Long id) {
        Optional<DealParty> party = partiesRepo.findById(id);
        return party.orElse(null);
    }

    public List<DealParty> partiesByDealId(Long id) {
        return partiesRepo.findPartiesByDealId(id);
    }

    public DealParty partyByDealIdAndCode(Long id, String code) {
        return partiesRepo.findPartyByDealIdAndCode(id, code);
    }

    ////////////////////Comment//////////////////////
    /////////////////////////////////////////////////

    public List<DealComment> comments() {
        return commRepo.findAll();
    }

    public DealComment commentById(Long id) {
        Optional<DealComment> comment = commRepo.findById(id);
        return comment.orElse(null);
    }

    public List<DealComment> commentsByDealId(Long id) {
        return commRepo.findCommentsByDealId(id);
    }

    public DealComment commentByDealAndType(Long id, String type) {
        return commRepo.findCommentByDealAndType(id, type);
    }

    ////////////////////Settlement////////////////////
    /////////////////////////////////////////////////

    public List<Settlement> settlements() {
        return settRepo.findAll();
    }

    public Settlement settlementById(Long id) {
        Optional<Settlement> settlement = settRepo.findById(id);
        return settlement.orElse(null);
    }

    public List<Settlement> settlementsByDealId(Long id) {
        return settRepo.findSettlementsByDealId(id);
    }

    public Settlement saveSettlement(SettlementDto settlementDto) {
        Settlement settlment = toEntity(settlementDto);
        settlment = settRepo.save(settlment);
        return settlment;
    }

    public SettlementDto updateSettlement(Long id, SettlementDto settlmentDto) {
        Optional<Settlement> existingSettlement = settRepo.findById(id);
        if (existingSettlement.isPresent()) {
            Settlement settlment = existingSettlement.get();
            updateEntityFromDto(settlmentDto, settlment);
            settlment = settRepo.save(settlment);
            return toDto(settlment);
        } else {
            return null; // Or throw an exception
        }
    }


    ///////////////////////////DTO TO ENTITY MAPPING////////////////
    ///////////////////////////////////////////////////////////////

    private InfoDeal mapDtoToEntity(InfoDealDto dto) {
        InfoDeal infoDeal = new InfoDeal();

        infoDeal.setLcAmount(dto.getLcAmount());
        infoDeal.setCurrencyID(dto.getCurrencyID());
        infoDeal.setExpiryPlace(dto.getExpiryPlace());
        infoDeal.setExpiryDate(dto.getExpiryDate());
        infoDeal.setVarAmountTolerance(dto.getVarAmountTolerance());
        infoDeal.setAddAmtCovered(dto.getAddAmtCovered());
        infoDeal.setDueDate(dto.getDueDate());
        infoDeal.setDraft(dto.getDraft());
        infoDeal.setDraftAt(dto.getDraftAt());
        infoDeal.setPresDay(dto.getPresDay());
        infoDeal.setTranshipment(dto.getTranshipment());
        infoDeal.setConfirmationCharge(dto.getConfirmationCharge());
        infoDeal.setFormLC(dto.getFormLC());
        infoDeal.setDocument(dto.getDocument());

        infoDeal.setPartialTranshipment(dto.getPartialTranshipment());

        return infoDeal;
    }

    private InfoDeal updateEntityFromDto(InfoDeal infoDeal, InfoDealDto dto) {
        infoDeal.setLcAmount(dto.getLcAmount());
        infoDeal.setCurrencyID(dto.getCurrencyID());
        infoDeal.setExpiryPlace(dto.getExpiryPlace());
        infoDeal.setExpiryDate(dto.getExpiryDate());
        infoDeal.setVarAmountTolerance(dto.getVarAmountTolerance());
        infoDeal.setAddAmtCovered(dto.getAddAmtCovered());
        infoDeal.setDueDate(dto.getDueDate());
        infoDeal.setDraft(dto.getDraft());
        infoDeal.setDraftAt(dto.getDraftAt());
        infoDeal.setPresDay(dto.getPresDay());
        infoDeal.setTranshipment(dto.getTranshipment());
        infoDeal.setConfirmationCharge(dto.getConfirmationCharge());
        infoDeal.setFormLC(dto.getFormLC());
        infoDeal.setDocument(dto.getDocument());

        infoDeal.setPartialTranshipment(dto.getPartialTranshipment());

        return infoDeal;

    }

    private SettlementDto toDto(Settlement settlment) {
        SettlementDto dto = new SettlementDto();
        dto.setId(settlment.getId());
        dto.setPaymentType(settlment.getPaymentType());
        dto.setAvailableWithBank(settlment.getAvailableWithBank());
        dto.setAvailableWithOther(settlment.getAvailableWithOther());
        dto.setMixedPay1(settlment.getMixedPay1());
        dto.setMixedPay2(settlment.getMixedPay2());
        dto.setMixedPay3(settlment.getMixedPay3());
        dto.setMixedPay4(settlment.getMixedPay4());
        dto.setNegDefPay1(settlment.getNegDefPay1());
        dto.setNegDefPay2(settlment.getNegDefPay2());
        dto.setNegDefPay3(settlment.getNegDefPay3());
        dto.setNegDefPay4(settlment.getNegDefPay4());
        dto.setIdDeal(settlment.getDeal().getId());
        return dto;
    }


    private Settlement toEntity(SettlementDto dto) {
        Settlement settlement = new Settlement();
        settlement.setPaymentType(dto.getPaymentType());
        settlement.setAvailableWithBank(dto.getAvailableWithBank());
        settlement.setAvailableWithOther(dto.getAvailableWithOther());
        settlement.setMixedPay1(dto.getMixedPay1());
        settlement.setMixedPay2(dto.getMixedPay2());
        settlement.setMixedPay3(dto.getMixedPay3());
        settlement.setMixedPay4(dto.getMixedPay4());
        settlement.setNegDefPay1(dto.getNegDefPay1());
        settlement.setNegDefPay2(dto.getNegDefPay2());
        settlement.setNegDefPay3(dto.getNegDefPay3());
        settlement.setNegDefPay4(dto.getNegDefPay4());
        // Assuming you have a method to fetch InfoDeal by id
        InfoDeal deal = fetchInfoDealById(dto.getIdDeal());
        settlement.setDeal(deal);
        return settlement;
    }

    private void updateEntityFromDto(SettlementDto dto, Settlement settlment) {
        settlment.setPaymentType(dto.getPaymentType());
        settlment.setAvailableWithBank(dto.getAvailableWithBank());
        settlment.setAvailableWithOther(dto.getAvailableWithOther());
        settlment.setMixedPay1(dto.getMixedPay1());
        settlment.setMixedPay2(dto.getMixedPay2());
        settlment.setMixedPay3(dto.getMixedPay3());
        settlment.setMixedPay4(dto.getMixedPay4());
        settlment.setNegDefPay1(dto.getNegDefPay1());
        settlment.setNegDefPay2(dto.getNegDefPay2());
        settlment.setNegDefPay3(dto.getNegDefPay3());
        settlment.setNegDefPay4(dto.getNegDefPay4());
        // Assuming you have a method to fetch InfoDeal by id
        InfoDeal deal = fetchInfoDealById(dto.getIdDeal());
        settlment.setDeal(deal);
    }

    private InfoDeal fetchInfoDealById(Long id) {
        return this.dealById(id);
    }
}
