package com.enegoce.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class InfoDeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //20 //Documentary Credit Number

    @Column(length = 24)
    private String formLC; //40A

    private LocalDate dueDate; //31C //Date of Issue

    private LocalDate expiryDate; //31D //Date of Expiry

    @Column(length = 29)
    private String expiryPlace; //31D

    //@Column(length = 20)
    private String bankISSRef; //Sender //51a

    //TODO: 40E ??

    @Column(length = 3)
    private String currencyID; //32B //Currency Code

    private BigDecimal lcAmount; //32B //Amount

    private BigDecimal varAmountTolerance; //39A //Percentage Credit Amt Tolerance

    @Column(length = 11)
    private String partialTranshipment; //43P //Partial Shipments

    @Column(length = 11)
    private String transhipment; //43T //Partial Transhipment

    private Integer presDay; //48

    private String confirmationCharge; //49

    private String addAmtCovered; //39C

    private String draftAt; //42C

    private String draft; //42a

    private String document; //46A

    @OneToMany(mappedBy = "id.deal", cascade = CascadeType.ALL)
    private List<DealParty> dealParties = new ArrayList<>();

    @OneToMany(mappedBy = "id.deal", cascade = CascadeType.ALL)
    private List<DealGoods> dealGoods = new ArrayList<>();

    @OneToMany(mappedBy = "id.deal", cascade = CascadeType.ALL)
    private List<DealComment> dealComments = new ArrayList<>();

    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL)
    private List<Settlement> settlements = new ArrayList<>();

    public InfoDeal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormLC() {
        return formLC;
    }

    public void setFormLC(String formLC) {
        this.formLC = formLC;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getExpiryPlace() {
        return expiryPlace;
    }

    public void setExpiryPlace(String expiryPlace) {
        this.expiryPlace = expiryPlace;
    }

    public String getBankISSRef() {
        return bankISSRef;
    }

    public void setBankISSRef(String bankISSRef) {
        this.bankISSRef = bankISSRef;
    }

    public String getCurrencyID() {
        return currencyID;
    }

    public void setCurrencyID(String currencyID) {
        this.currencyID = currencyID;
    }

    public BigDecimal getLcAmount() {
        return lcAmount;
    }

    public void setLcAmount(BigDecimal lcAmount) {
        this.lcAmount = lcAmount;
    }

    public BigDecimal getVarAmountTolerance() {
        return varAmountTolerance;
    }

    public void setVarAmountTolerance(BigDecimal varAmountTolerance) {
        this.varAmountTolerance = varAmountTolerance;
    }

    public String getPartialTranshipment() {
        return partialTranshipment;
    }

    public void setPartialTranshipment(String partialTranshipment) {
        this.partialTranshipment = partialTranshipment;
    }

    public String getTranshipment() {
        return transhipment;
    }

    public void setTranshipment(String transhipment) {
        this.transhipment = transhipment;
    }

    public Integer getPresDay() {
        return presDay;
    }

    public void setPresDay(Integer presDay) {
        this.presDay = presDay;
    }

    public String getConfirmationCharge() {
        return confirmationCharge;
    }

    public void setConfirmationCharge(String confirmationCharge) {
        this.confirmationCharge = confirmationCharge;
    }

    public String getAddAmtCovered() {
        return addAmtCovered;
    }

    public void setAddAmtCovered(String addAmtCovered) {
        this.addAmtCovered = addAmtCovered;
    }

    public String getDraftAt() {
        return draftAt;
    }

    public void setDraftAt(String draftAt) {
        this.draftAt = draftAt;
    }

    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public List<DealParty> getDealParties() {
        return dealParties;
    }

    public void setDealParties(List<DealParty> dealParties) {
        this.dealParties = dealParties;
    }

    public List<DealGoods> getDealGoods() {
        return dealGoods;
    }

    public void setDealGoods(List<DealGoods> dealGoods) {
        this.dealGoods = dealGoods;
    }

    public List<Settlement> getSettlements() {
        return settlements;
    }

    public void setSettlements(List<Settlement> settlements) {
        this.settlements = settlements;
    }

    public List<DealComment> getDealComments() {
        return dealComments;
    }

    public void setDealComments(List<DealComment> dealComments) {
        this.dealComments = dealComments;
    }

    public InfoDeal(Long id, String formLC, LocalDate dueDate, LocalDate expiryDate, String expiryPlace, String bankISSRef, String currencyID, BigDecimal lcAmount, BigDecimal varAmountTolerance, String partialTranshipment, String transhipment, Integer presDay, String confirmationCharge, String addAmtCovered, String draftAt, String draft, String document, List<DealParty> dealParties, List<DealGoods> dealGoods, List<Settlement> settlements, List<DealComment> dealComments) {
        this.id = id;
        this.formLC = formLC;
        this.dueDate = dueDate;
        this.expiryDate = expiryDate;
        this.expiryPlace = expiryPlace;
        this.bankISSRef = bankISSRef;
        this.currencyID = currencyID;
        this.lcAmount = lcAmount;
        this.varAmountTolerance = varAmountTolerance;
        this.partialTranshipment = partialTranshipment;
        this.transhipment = transhipment;
        this.presDay = presDay;
        this.confirmationCharge = confirmationCharge;
        this.addAmtCovered = addAmtCovered;
        this.draftAt = draftAt;
        this.draft = draft;
        this.document = document;
        this.dealParties = dealParties;
        this.dealGoods = dealGoods;
        this.settlements = settlements;
        this.dealComments = dealComments;
    }
}
