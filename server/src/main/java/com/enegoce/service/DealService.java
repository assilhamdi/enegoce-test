package com.enegoce.service;

import com.enegoce.entities.*;
import com.enegoce.repository.*;
import com.engoce.deal.dto.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    public Long saveInfoDeal(InfoDealDto infoDealDto) {
        InfoDeal infoDeal = infoDealToEntity(infoDealDto);
        InfoDeal savedInfoDeal = dealRepo.save(infoDeal);
        return savedInfoDeal.getId(); // Assuming getId() returns the ID of InfoDeal entity
    }

    private InfoDeal infoDealToEntity(InfoDealDto dto) {
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
        infoDeal.setBankISSRef(dto.getBankISSRef());

        return infoDeal;
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

    @Transactional
    public void saveDealGoodsList(List<DealGoodsDto> dealGoodsDtoList, Long infoDealId) {
        for (DealGoodsDto goodDto : dealGoodsDtoList) {
            DealGoods dealGoods = convertGoodsToEntity(goodDto, infoDealId);
            goodsRepo.save(dealGoods);
        }
    }

    private DealGoods convertGoodsToEntity(DealGoodsDto dealGoodsDTO, Long infoDealId) {
        // Find the InfoDeal entity
        InfoDeal deal = dealRepo.findById(infoDealId)
                .orElseThrow(() -> new RuntimeException(String.format("Deal %s not found", dealGoodsDTO.getId().idDeal())));

        // Find the max sequence number for the given InfoDeal
        Long maxSeq = goodsRepo.findMaxSeqByDeal(deal);

        // Generate the new sequence number
        Long newSeq = (maxSeq == null) ? 1L : maxSeq + 1L;

        // Create the primary key
        DealGoodsPK dealGoodsPK = new DealGoodsPK();
        dealGoodsPK.setDeal(deal);
        dealGoodsPK.setSeq(dealGoodsDTO.getId().seq());
        dealGoodsPK.setStepId(dealGoodsDTO.getId().stepId());

        // Create and return the DealGoods entity
        return DealGoods.builder()
                .id(dealGoodsPK)
                .goodsDesc(dealGoodsDTO.getGoodsDesc())
                .placeOfTakingCharge(dealGoodsDTO.getPlaceOfTakingCharge())
                .portOfLoading(dealGoodsDTO.getPortOfLoading())
                .portOfDischarge(dealGoodsDTO.getPortOfDischarge())
                .placeOfFinalDestination(dealGoodsDTO.getPlaceOfFinalDestination())
                .shipmentDateLast(dealGoodsDTO.getShipmentDateLast())
                .shipmentPeriod(dealGoodsDTO.getShipmentPeriod())
                .build();
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

    @Transactional
    public void saveDealPartyList(List<DealPartyDto> dealPartyDtoList, Long infoDealId) {
       for (DealPartyDto partyDto : dealPartyDtoList) {
           DealParty dealParty = convertPartyToEntity(partyDto, infoDealId);
           partiesRepo.save(dealParty);
       }
    }

    private DealParty convertPartyToEntity(DealPartyDto dealPartyDto, Long infoDealId) {
        // Find the InfoDeal entity
        InfoDeal deal = dealRepo.findById(infoDealId)
                .orElseThrow(() -> new RuntimeException(String.format("Deal %s not found", dealPartyDto.getId().idDeal())));

        // Find the max sequence number for the given InfoDeal
        Long maxSeq = partiesRepo.findMaxSeqByDeal(deal);

        // Generate the new sequence number
        Long newSeq = (maxSeq == null) ? 1L : maxSeq + 1L;

        // Create the primary key
        DealPartyPK dealPartyPK = new DealPartyPK();
        dealPartyPK.setDeal(deal);
        dealPartyPK.setSeq(newSeq);
        dealPartyPK.setCodPrt(dealPartyDto.getId().codPrt());

        // Create and return the DealParty entity
        return DealParty.builder()
                .id(dealPartyPK)
                .nom(dealPartyDto.getNom())
                .street1(dealPartyDto.getStreet1())
                .street2(dealPartyDto.getStreet2())
                .street3(dealPartyDto.getStreet3())
                .town(dealPartyDto.getTown())
                .country(dealPartyDto.getCountry())
                .build();
    }


    ////////////////////Comment//////////////////////
    /////////////////////////////////////////////////

    public List<DealComment> commentsByDealId(Long id) {
        return commRepo.findCommentsByDealId(id);
    }

    public DealComment commentByDealAndType(Long id, String type) {
        return commRepo.findCommentByDealAndType(id, type);
    }

    @Transactional
    public void saveDealCommentList(List<DealCommentDto> dealCommentDtoList, Long infoDealId) {
        // Iterate through each DealCommentDto and save it individually
        for (DealCommentDto dealCommentDto : dealCommentDtoList) {
            DealComment dealComment = convertCommToEntity(dealCommentDto, infoDealId);
            commRepo.save(dealComment); // Save each DealComment entity
        }
    }

    private DealComment convertCommToEntity(DealCommentDto dealCommentDto, Long infoDealId) {
        // Find the InfoDeal entity
        InfoDeal deal = dealRepo.findById(infoDealId)
                .orElseThrow(() -> new RuntimeException(String.format("Deal %s not found", dealCommentDto.getId().idDeal())));

        // Find the max sequence number for the given InfoDeal
        Long maxSeq = commRepo.findMaxSeqByDeal(deal);

        // Generate the new sequence number
        Long newSeq = (maxSeq == null) ? 1L : maxSeq + 1L;

        // Create the primary key
        DealCommentPK dealCommentPK = new DealCommentPK();
        dealCommentPK.setDeal(deal);
        dealCommentPK.setSeq(newSeq);

        // Create and return the DealComment entity
        return DealComment.builder()
                .id(dealCommentPK)
                .typeComt(dealCommentDto.getTypeComt())
                .comment(dealCommentDto.getComment())
                .dateCreation(LocalDate.now())
                .useName("defaultUser") // Set this accordingly
                .stepId("DRAFT") // Set this accordingly
                .build();
    }

    ////////////////////Settlement////////////////////
    /////////////////////////////////////////////////

    public Settlement settlementById(Long id) {
        Optional<Settlement> settlement = settRepo.findById(id);
        return settlement.orElse(null);
    }

    public List<Settlement> settlementsByDealId(Long id) {
        return settRepo.findSettlementsByDealId(id);
    }

    @Transactional
    public void saveSettlementList(List<SettlementDto> settlementDtoList, Long infoDealId) {
        for (SettlementDto settlementDto : settlementDtoList) {
            Settlement sett = settToEntity(settlementDto, infoDealId);
            settRepo.save(sett);
        }
    }

    private Settlement settToEntity(SettlementDto dto, Long infoDealId) {
        Settlement settlement = new Settlement();
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

        // Find the InfoDeal entity by ID
        InfoDeal deal = dealRepo.findById(infoDealId)
                .orElseThrow(() -> new RuntimeException(String.format("Deal %d not found", infoDealId)));

        settlement.setDeal(deal);
        return settlement;
    }

}



