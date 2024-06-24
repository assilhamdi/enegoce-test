package com.engoce.deal.dto;

public class SettlementDto {

    private Long id;
    private String paymentType;
    private String availableWithBank;
    private String availableWithOther;
    private String mixedPay1;
    private String mixedPay2;
    private String mixedPay3;
    private String mixedPay4;
    private String negDefPay1;
    private String negDefPay2;
    private String negDefPay3;
    private String negDefPay4;
    private Long idDeal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getAvailableWithBank() {
        return availableWithBank;
    }

    public void setAvailableWithBank(String availableWithBank) {
        this.availableWithBank = availableWithBank;
    }

    public String getAvailableWithOther() {
        return availableWithOther;
    }

    public void setAvailableWithOther(String availableWithOther) {
        this.availableWithOther = availableWithOther;
    }

    public String getMixedPay1() {
        return mixedPay1;
    }

    public void setMixedPay1(String mixedPay1) {
        this.mixedPay1 = mixedPay1;
    }

    public String getMixedPay2() {
        return mixedPay2;
    }

    public void setMixedPay2(String mixedPay2) {
        this.mixedPay2 = mixedPay2;
    }

    public String getMixedPay3() {
        return mixedPay3;
    }

    public void setMixedPay3(String mixedPay3) {
        this.mixedPay3 = mixedPay3;
    }

    public String getMixedPay4() {
        return mixedPay4;
    }

    public void setMixedPay4(String mixedPay4) {
        this.mixedPay4 = mixedPay4;
    }

    public String getNegDefPay1() {
        return negDefPay1;
    }

    public void setNegDefPay1(String negDefPay1) {
        this.negDefPay1 = negDefPay1;
    }

    public String getNegDefPay2() {
        return negDefPay2;
    }

    public void setNegDefPay2(String negDefPay2) {
        this.negDefPay2 = negDefPay2;
    }

    public String getNegDefPay3() {
        return negDefPay3;
    }

    public void setNegDefPay3(String negDefPay3) {
        this.negDefPay3 = negDefPay3;
    }

    public String getNegDefPay4() {
        return negDefPay4;
    }

    public void setNegDefPay4(String negDefPay4) {
        this.negDefPay4 = negDefPay4;
    }

    public Long getIdDeal() {
        return idDeal;
    }

    public void setIdDeal(Long idDeal) {
        this.idDeal = idDeal;
    }
}
