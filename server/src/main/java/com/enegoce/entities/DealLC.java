package com.enegoce.entities;

import com.enegoce.validation.XType;
import jakarta.persistence.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class DealLC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dealId; //20: //Documentary Credit Number //TODO: Validation

    @Column(length = 24)
    @Length(max = 24, message = "Max length allowed : 24")
    private String formLC; //40A: //Form of Documentary Credit //X
    private Date dueDate; //31C: //Date of Issue

    //TODO: 40E ??
    private Date expiryDate; //31D: //Date of Expiry


    @Column(length = 29)
    @Length(max = 29, message = "Max length allowed : 29")
    private String expiryPlace; //31D: //Place of Expiry //X //TODO: ???

    @Column(length = 144) // 4 lines of 35 characters each including newline characters
    @Length(max = 144, message = "Max length allowed : 144")
    private String customerReference; //Applicant //50: //X


    @Column(length = 178) // 4 lines of 35 characters each including newline characters + 34
    @Length(max = 178, message = "Max length allowed : 178")
    private String counterParty; //Beneficiary - Name & Address //59: //X //TODO: With Address ??

    //@Column(length = 20)
    private String bankISSRef; //Sender //51a: //TODO: A or D

    //@Column(length = 20)
    private String bankRMBRef; //Receiver //53a: //TODO: A or D

    private Timestamp creationDate; //Auto-Generated

    //@Column(length = 3)
    private String currencyId; //32B: //Currency Code //TODO
    //@Column(precision = 13, scale = 2)
    private BigDecimal lcAmount; //32B: //Amount //TODO
    //@Column(precision = 13, scale = 2)
    private BigDecimal varAmountTolerance; //39A: //Percentage Credit Amt Tolerance //TODO: n?

    /*@Column(precision = 13, scale = 2)
    private BigDecimal addAmtCovered;*/

    //@Column(length = 3)
    private String availableWith; //41A: //Available With //A or D


    @Column(length = 11)
    @Length(max = 11, message = "Max length allowed : 11")
    private String partialTranshipment; //43P //Partial Shipments //X

    @Column(length = 11)
    @Length(max = 11, message = "Max length allowed : 11")
    private String transhipment; //43T //Partial Transhipment //X

    @OneToMany(mappedBy = "dealLC", cascade = CascadeType.ALL)
    private List<DealParty> dealParties = new ArrayList<>();

    @OneToMany(mappedBy = "dealLC", cascade = CascadeType.ALL)
    private List<DealGoods> dealGoods = new ArrayList<>();

    @OneToMany(mappedBy = "dealLC", cascade = CascadeType.ALL)
    private List<DealDoc> dealDocs = new ArrayList<>();

    @OneToMany(mappedBy = "dealLC", cascade = CascadeType.ALL)
    private List<DealComment> dealComments = new ArrayList<>();

    @OneToMany(mappedBy = "dealLC", cascade = CascadeType.ALL)
    private List<DealStep> dealSteps = new ArrayList<>();


    //TODO:  Mixed Pay list no List<> of mixed pays db multiple fields, same for NEGDEFPAY


    public Integer getDealId() {
        return dealId;
    }

    public void setDealId(Integer dealId) {
        this.dealId = dealId;
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

    public void setCustomerReference(String costumerReference) {
        this.customerReference = costumerReference;
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

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
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

    public String getAvailableWith() {
        return availableWith;
    }

    public void setAvailableWith(String availableWith) {
        this.availableWith = availableWith;
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

    public DealLC() {
    }

    public DealLC(Integer dealId, String formLC, Date dueDate, Date expiryDate,
                  String expiryPlace, String customerReference, String counterParty,
                  String bankISSRef, String bankRMBRef, Timestamp creationDate,
                  String currencyId, BigDecimal lcAmount, BigDecimal varAmountTolerance,
                  String availableWith, String partialTranshipment, String transhipment,
                  List<DealParty> dealParties, List<DealGoods> dealGoods, List<DealDoc> dealDocs,
                  List<DealComment> dealComments, List<DealStep> dealSteps) {
        this.dealId = dealId;
        this.formLC = formLC;
        this.dueDate = dueDate;
        this.expiryDate = expiryDate;
        this.expiryPlace = expiryPlace;
        this.customerReference = customerReference;
        this.counterParty = counterParty;
        this.bankISSRef = bankISSRef;
        this.bankRMBRef = bankRMBRef;
        this.creationDate = creationDate;
        this.currencyId = currencyId;
        this.lcAmount = lcAmount;
        this.varAmountTolerance = varAmountTolerance;
        this.availableWith = availableWith;
        this.partialTranshipment = partialTranshipment;
        this.transhipment = transhipment;
        this.dealParties = dealParties;
        this.dealGoods = dealGoods;
        this.dealDocs = dealDocs;
        this.dealComments = dealComments;
        this.dealSteps = dealSteps;
    }
}
