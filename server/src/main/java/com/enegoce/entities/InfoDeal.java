package com.enegoce.entities;

import jakarta.persistence.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class InfoDeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //20: //Documentary Credit Number

    @Column(length = 24)
    @Length(max = 24, message = "Max length allowed : 24")
    private String formLC; //40A: //Form of Documentary Credit //X

    private Date dueDate; //31C: //Date of Issue

    //TODO: 40E ??
    private Date expiryDate; //31D: //Date of Expiry

    @Column(length = 29)
    @Length(max = 29, message = "Max length allowed : 29")
    private String expiryPlace; //31D: //Place of Expiry //X

    @Column(length = 144) // 4 lines of 35 characters each including newline characters
    @Length(max = 144, message = "Max length allowed : 144")
    private String customerReference; //Applicant //50: //X

    @Column(length = 178) // 4 lines of 35 characters each including newline characters + 34
    @Length(max = 178, message = "Max length allowed : 178")
    private String counterParty; //Beneficiary - Name & Address //59: //X

    //@Column(length = 20)
    private String bankISSRef; //Sender //51a:

    //@Column(length = 20)
    private String bankRMBRef; //Receiver //53a:

    private Timestamp creationDate; //Auto-Generated

    //@Column(length = 3)
    private String currencyID; //32B: //Currency Code
    //@Column(precision = 13, scale = 2)
    private BigDecimal lcAmount; //32B: //Amount
    //@Column(precision = 13, scale = 2)
    private BigDecimal varAmountTolerance; //39A: //Percentage Credit Amt Tolerance

    @Column(length = 11)
    @Length(max = 11, message = "Max length allowed : 11")
    private String partialTranshipment; //43P //Partial Shipments //X

    @Column(length = 11)
    @Length(max = 11, message = "Max length allowed : 11")
    private String transhipment; //43T //Partial Transhipment //X

    private Integer presDay; //48

    private String confirmationCharge; //49

    private String addAmtCovered; //39C

    private String draftAt; //42C

    private String draft; //42a

    private String document; //46A

    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL)
    private List<DealParty> dealParties = new ArrayList<>();

    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL)
    private List<DealGoods> dealGoods = new ArrayList<>();

    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL)
    private List<Settlement> settlements = new ArrayList<>();

    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL)
    private List<DealDoc> dealDocs = new ArrayList<>();

    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL)
    private List<DealComment> dealComments = new ArrayList<>();

    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL)
    private List<DealStep> dealSteps = new ArrayList<>();

    public InfoDeal() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFormLC() {
        return formLC;
    }

    public void setFormLC(String formLC) {
        this.formLC = formLC;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getExpiryPlace() {
        return expiryPlace;
    }

    public void setExpiryPlace(String expiryPlace) {
        this.expiryPlace = expiryPlace;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public String getCounterParty() {
        return counterParty;
    }

    public void setCounterParty(String counterParty) {
        this.counterParty = counterParty;
    }

    public String getBankISSRef() {
        return bankISSRef;
    }

    public void setBankISSRef(String bankISSRef) {
        this.bankISSRef = bankISSRef;
    }

    public String getBankRMBRef() {
        return bankRMBRef;
    }

    public void setBankRMBRef(String bankRMBRef) {
        this.bankRMBRef = bankRMBRef;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
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

    public List<DealDoc> getDealDocs() {
        return dealDocs;
    }

    public void setDealDocs(List<DealDoc> dealDocs) {
        this.dealDocs = dealDocs;
    }

    public List<DealComment> getDealComments() {
        return dealComments;
    }

    public void setDealComments(List<DealComment> dealComments) {
        this.dealComments = dealComments;
    }

    public List<DealStep> getDealSteps() {
        return dealSteps;
    }

    public void setDealSteps(List<DealStep> dealSteps) {
        this.dealSteps = dealSteps;
    }

    public InfoDeal(Integer id, String formLC, Date dueDate, Date expiryDate, String expiryPlace, String customerReference, String counterParty, String bankISSRef, String bankRMBRef, Timestamp creationDate, String currencyID, BigDecimal lcAmount, BigDecimal varAmountTolerance, String partialTranshipment, String transhipment, Integer presDay, String confirmationCharge, String addAmtCovered, String draftAt, String draft, String document, List<DealParty> dealParties, List<DealGoods> dealGoods, List<Settlement> settlements, List<DealDoc> dealDocs, List<DealComment> dealComments, List<DealStep> dealSteps) {
        this.id = id;
        this.formLC = formLC;
        this.dueDate = dueDate;
        this.expiryDate = expiryDate;
        this.expiryPlace = expiryPlace;
        this.customerReference = customerReference;
        this.counterParty = counterParty;
        this.bankISSRef = bankISSRef;
        this.bankRMBRef = bankRMBRef;
        this.creationDate = creationDate;
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
        this.dealDocs = dealDocs;
        this.dealComments = dealComments;
        this.dealSteps = dealSteps;
    }
}
