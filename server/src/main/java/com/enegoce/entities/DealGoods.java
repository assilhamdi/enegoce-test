package com.enegoce.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class DealGoods implements Serializable {

    @EmbeddedId
    private DealGoodsPK id;

    private String productCode;

    private String productName;

    private String productOrigine;

    @Column(length = 6500)
    private String goodsDesc;

    @Column(precision = 13, scale = 2)
    private BigDecimal unitPrice;

    @Column(length = 20)
    private int quantity;

    @Column(precision = 13, scale = 2)
    private BigDecimal totalAmount;

    @Column(length = 50)
    private String placeOfTakingCharge;

    @Column(length = 50)
    private String portOfLoading;

    @Column(length = 50)
    private String portOfDischarge;

    @Column(length = 50)
    private String placeOfFinalDestination;

    private Date shipmentDateLast;

    private Integer shipmentPeriod;

    public DealGoods() {
    }

    public DealGoodsPK getId() {
        return id;
    }

    public void setId(DealGoodsPK id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductOrigine() {
        return productOrigine;
    }

    public void setProductOrigine(String productOrigine) {
        this.productOrigine = productOrigine;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPlaceOfTakingCharge() {
        return placeOfTakingCharge;
    }

    public void setPlaceOfTakingCharge(String placeOfTakingCharge) {
        this.placeOfTakingCharge = placeOfTakingCharge;
    }

    public String getPortOfLoading() {
        return portOfLoading;
    }

    public void setPortOfLoading(String portOfLoading) {
        this.portOfLoading = portOfLoading;
    }

    public String getPortOfDischarge() {
        return portOfDischarge;
    }

    public void setPortOfDischarge(String portOfDischarge) {
        this.portOfDischarge = portOfDischarge;
    }

    public String getPlaceOfFinalDestination() {
        return placeOfFinalDestination;
    }

    public void setPlaceOfFinalDestination(String placeOfFinalDestination) {
        this.placeOfFinalDestination = placeOfFinalDestination;
    }

    public Date getShipmentDateLast() {
        return shipmentDateLast;
    }

    public void setShipmentDateLast(Date shipmentDateLast) {
        this.shipmentDateLast = shipmentDateLast;
    }

    public Integer getShipmentPeriod() {
        return shipmentPeriod;
    }

    public void setShipmentPeriod(Integer shipmentPeriod) {
        this.shipmentPeriod = shipmentPeriod;
    }

    public DealGoods(DealGoodsPK id, String productCode, String productName, String productOrigine, String goodsDesc, BigDecimal unitPrice, int quantity, BigDecimal totalAmount, String placeOfTakingCharge, String portOfLoading, String portOfDischarge, String placeOfFinalDestination, Date shipmentDateLast, Integer shipmentPeriod) {
        this.id = id;
        this.productCode = productCode;
        this.productName = productName;
        this.productOrigine = productOrigine;
        this.goodsDesc = goodsDesc;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.placeOfTakingCharge = placeOfTakingCharge;
        this.portOfLoading = portOfLoading;
        this.portOfDischarge = portOfDischarge;
        this.placeOfFinalDestination = placeOfFinalDestination;
        this.shipmentDateLast = shipmentDateLast;
        this.shipmentPeriod = shipmentPeriod;
    }
}
