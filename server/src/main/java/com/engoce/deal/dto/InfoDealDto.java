package com.engoce.deal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;


public class InfoDealDto {

    private Integer id; // Documentary Credit Number
    private String formLC; // 40A
    private LocalDate dueDate; // 31C - Date of Issue
    private LocalDate expiryDate; // 31D - Date of Expiry
    private String expiryPlace; // 31D
    private String bankISSRef; // Sender - 51a
    private String currencyID; // 32B - Currency Code
    private BigDecimal lcAmount; // 32B - Amount
    private BigDecimal varAmountTolerance; // 39A - Percentage Credit Amt Tolerance
    private String partialTranshipment; // 43P - Partial Shipments
    private String transhipment; // 43T - Partial Transhipment
    private Integer presDay; // 48
    private String confirmationCharge; // 49
    private String addAmtCovered; // 39C
    private String draftAt; // 42C
    private String draft; // 42a
    private String document; // 46A

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
}

