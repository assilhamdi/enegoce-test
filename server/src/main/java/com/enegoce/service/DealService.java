package com.enegoce.service;

import com.enegoce.entities.*;
import com.enegoce.repository.*;
import com.engoce.deal.dto.*;
import jakarta.persistence.EntityNotFoundException;
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

    public void saveInfoDeal(InfoDealDto infoDealDto) {
        InfoDeal infoDeal = mapDtoToEntity(infoDealDto);
        dealRepo.save(infoDeal);
    }

    public InfoDeal updateInfoDeal(Long id, InfoDealDto infoDealDto) {
        Optional<InfoDeal> optionalInfoDeal = dealRepo.findById(id);
        if (optionalInfoDeal.isPresent()) {
            InfoDeal infoDeal = optionalInfoDeal.get();
            updateSettEntityFromDto(infoDeal, infoDealDto);
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

    public DealGoods saveDealGoods(DealGoodsDto dealGoodsDTO) {
        DealGoods dealGoods = convertGoodsToEntity(dealGoodsDTO);
        return goodsRepo.save(dealGoods);
    }

    public void saveDealGoodsList(List<DealGoodsDto> dealGoodsDtoList) {
        List<DealGoods> dealGoodsList = dealGoodsDtoList.stream()
                .map(this::convertGoodsToEntity)
                .collect(Collectors.toList());
        goodsRepo.saveAll(dealGoodsList);
    }

    private DealGoods convertGoodsToEntity(DealGoodsDto dealGoodsDTO) {
        DealGoodsPK dealGoodsPK = new DealGoodsPK();
        InfoDeal deal = dealRepo.findById(dealGoodsDTO.getId().idDeal())
                .orElseThrow(() -> new RuntimeException(String.format("Deal %s not found", dealGoodsDTO.getId().idDeal())));
        dealGoodsPK.setDeal(deal);
        dealGoodsPK.setSeq(dealGoodsDTO.getId().seq());
        dealGoodsPK.setStepId(dealGoodsDTO.getId().stepId());

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

    public DealParty saveDealParty(DealPartyDto dealPartyDTO) {
        DealParty dealParty = convertPartyToEntity(dealPartyDTO);
        return partiesRepo.save(dealParty);
    }

    public void saveDealPartyList(List<DealPartyDto> dealPartyDtoList) {
        List<DealParty> dealPartyList = dealPartyDtoList.stream()
                .map(this::convertPartyToEntity)
                .collect(Collectors.toList());
        partiesRepo.saveAll(dealPartyList);
    }

    private DealParty convertPartyToEntity(DealPartyDto dealPartyDTO) {
        DealPartyPK dealPartyPK = new DealPartyPK();
        InfoDeal deal = dealRepo.findById(dealPartyDTO.getId().idDeal())
                .orElseThrow(() -> new RuntimeException(String.format("DealParty %s not found", dealPartyDTO.getId().idDeal())));
        dealPartyPK.setDeal(deal);
        dealPartyPK.setSeq(dealPartyDTO.getId().seq());
        dealPartyPK.setCodPrt(dealPartyDTO.getId().codPrt());

        return DealParty.builder()
                .id(dealPartyPK)
                .nom(dealPartyDTO.getNom())
                .street1(dealPartyDTO.getStreet1())
                .street2(dealPartyDTO.getStreet2())
                .town(dealPartyDTO.getTown())
                .country(dealPartyDTO.getCountry())
                .dateCreation(LocalDate.now())
                .dateUpdated(LocalDate.now())
                .stepId("DRAFT")
                .build();
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

    public DealComment saveDealComment(DealCommentDto dealCommentDTO) {
        DealComment dealComment = convertCommToEntity(dealCommentDTO);
        return commRepo.save(dealComment);
    }

    public void saveDealCommentList(List<DealCommentDto> dealCommentDtoList) {
        List<DealComment> dealCommentList = dealCommentDtoList.stream()
                .map(this::convertCommToEntity)
                .collect(Collectors.toList());
        commRepo.saveAll(dealCommentList);
    }

    private DealComment convertCommToEntity(DealCommentDto dealCommentDTO) {
        DealCommentPK dealCommentPK = new DealCommentPK();
        InfoDeal deal = dealRepo.findById(dealCommentDTO.getId().idDeal())
                .orElseThrow(() -> new RuntimeException(String.format("Deal %s not found", dealCommentDTO.getId().idDeal())));
        dealCommentPK.setDeal(deal);
        dealCommentPK.setSeq(dealCommentDTO.getId().seq());

        return DealComment.builder()
                .id(dealCommentPK)
                .typeComt(dealCommentDTO.getTypeComt())
                .comment(dealCommentDTO.getComment())
                .dateCreation(LocalDate.now())
                .stepId("DRAFT")
                .build();
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
        Settlement settlment = SettToEntity(settlementDto);
        settlment = settRepo.save(settlment);
        return settlment;
    }

    public void saveSettlementList(List<SettlementDto> settlementDtoList) {
        List<Settlement> settlementList = settlementDtoList.stream()
                .map(this::SettToEntity)
                .collect(Collectors.toList());
        settRepo.saveAll(settlementList);
    }

    public SettlementDto updateSettlement(Long id, SettlementDto settlmentDto) {
        Optional<Settlement> existingSettlement = settRepo.findById(id);
        if (existingSettlement.isPresent()) {
            Settlement settlment = existingSettlement.get();
            updateSettEntityFromDto(settlmentDto, settlment);
            settlment = settRepo.save(settlment);
            return settToDto(settlment);
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

    private InfoDeal updateSettEntityFromDto(InfoDeal infoDeal, InfoDealDto dto) {
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

    private SettlementDto settToDto(Settlement settlment) {
        SettlementDto dto = new SettlementDto();
        dto.setId(settlment.getId());
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


    private Settlement SettToEntity(SettlementDto dto) {
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
        InfoDeal deal = this.dealById(dto.getIdDeal());
        settlement.setDeal(deal);
        return settlement;
    }

    private void updateSettEntityFromDto(SettlementDto dto, Settlement settlment) {
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
        InfoDeal deal = this.dealById(dto.getIdDeal());
        settlment.setDeal(deal);
    }

    //////////////////////////////////////////////////
    //////////////////////////////////////////////////


    public DealGoodsDto getOrCreateDealGoods(List<DealGoodsDto> dealGoodsList, Long dealId) {
        Optional<DealGoodsDto> existing = dealGoodsList.stream()
                .filter(goods -> goods.getId().idDeal().equals(dealId))
                .findFirst();

        if (existing.isPresent()) {
            return existing.get();
        } else {
            DealGoodsDto newDealGoods = new DealGoodsDto();
            newDealGoods.setId(new DealGoodsPKID(dealId, "", 0L));
            newDealGoods.setGoodsDesc("");
            newDealGoods.setPlaceOfTakingCharge("");
            newDealGoods.setPortOfLoading("");
            newDealGoods.setPortOfDischarge("");
            newDealGoods.setPlaceOfFinalDestination("");
            newDealGoods.setShipmentDateLast(new Date());
            newDealGoods.setShipmentPeriod(0);
            return newDealGoods;
        }
    }

    public DealPartyDto getOrCreateDealParty(List<DealPartyDto> dealPartiesList, Long dealId, String codPrt) {
        Optional<DealPartyDto> existing = dealPartiesList.stream()
                .filter(party -> party.getId().idDeal().equals(dealId) && party.getId().codPrt().equals(codPrt))
                .findFirst();

        if (existing.isPresent()) {
            return existing.get();
        } else {
            DealPartyDto newDealParty = new DealPartyDto();
            newDealParty.setId(new DealPartyPKID(dealId, 0L, codPrt));
            newDealParty.setNom("");
            newDealParty.setStreet1("");
            newDealParty.setStreet2("");
            newDealParty.setStreet3("");
            newDealParty.setTown("");
            newDealParty.setCountry("");
            return newDealParty;
        }
    }

    public SettlementDto getOrCreateSettlement(List<SettlementDto> settlementList, Long id) {
        Optional<SettlementDto> existing = settlementList.stream()
                .filter(settlement -> settlement.getId().equals(id))
                .findFirst();

        if (existing.isPresent()) {
            return existing.get();
        } else {
            SettlementDto newSettlement = new SettlementDto();
            newSettlement.setId(id);
            newSettlement.setAvailableWithBank("");
            newSettlement.setAvailableWithOther("");
            newSettlement.setMixedPay1("");
            newSettlement.setMixedPay2("");
            newSettlement.setMixedPay3("");
            newSettlement.setMixedPay4("");
            newSettlement.setNegDefPay1("");
            newSettlement.setNegDefPay2("");
            newSettlement.setNegDefPay3("");
            newSettlement.setNegDefPay4("");
            newSettlement.setIdDeal(0L);
            return newSettlement;
        }
    }

    public DealCommentDto getOrCreateDealComment(List<DealCommentDto> dealCommentsList, Long dealId, String type) {
        Optional<DealCommentDto> existing = dealCommentsList.stream()
                .filter(comment -> comment.getId().idDeal().equals(dealId) && comment.getTypeComt().equals(type))
                .findFirst();

        if (existing.isPresent()) {
            return existing.get();
        } else {
            DealCommentDto newDealComment = new DealCommentDto();
            newDealComment.setId(new DealCommentPKID(dealId, 0L));
            newDealComment.setComment("");
            newDealComment.setTypeComt(type);
            return newDealComment;
        }
    }

    public void saveEntityList(List<Object> entityDtoList) throws Exception {
        for (Object entityDto : entityDtoList) {
            if (entityDto instanceof DealGoodsDto) {
                List<DealGoods> dealGoodsList = Collections.singletonList(convertGoodsToEntity((DealGoodsDto) entityDto));
                goodsRepo.saveAll(dealGoodsList);
            } else if (entityDto instanceof DealPartyDto) {
                List<DealParty> dealPartyList = Collections.singletonList(convertPartyToEntity((DealPartyDto) entityDto));
                partiesRepo.saveAll(dealPartyList);
            } else if (entityDto instanceof DealCommentDto) {
                List<DealComment> dealCommentList = Collections.singletonList(convertCommToEntity((DealCommentDto) entityDto));
                commRepo.saveAll(dealCommentList);
            } else if (entityDto instanceof SettlementDto) {
                List<Settlement> settlementList = Collections.singletonList(SettToEntity((SettlementDto) entityDto));
                settRepo.saveAll(settlementList);
            } else {
                throw new Exception("Unsupported entity type: " + entityDto.getClass().getSimpleName());
            }
        }
    }

}



