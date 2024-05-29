package com.enegoce.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class DealGoods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "dealLcId")
    private DealLC dealLC = new DealLC();

    @Column(length = 6500)
    private String goodsDesc; // Descriptions of goods // Varchar(6500)

    @Column(length = 5)
    private String stepID; // Etape du deal // Varchar(5)

    private Integer seq; // Sequence de la modification // Number(3)

    private String goodsCODE;

    private String goodsType;

    @Column(length = 2)
    private String goodsOrigin; // Origin of Goods // varchar(2)

    @Column(length = 50)
    private String placeOfTakingCharge; // Place of taking charge // Varchar(50)

    @Column(length = 50)
    private String portOfLoading; // Port of Loading or Airport of departure 44E // Varchar(50)

    @Column(length = 50)
    private String portOfDischarge; // Port of Discharge 44F // Varchar(50)

    @Column(length = 50)
    private String placeOfFinalDestination; // place of final destination 44 // Varchar(50)

    @Column(length = 3)
    private String transportationType; // transportation type (Air/SEA) list // Varchar(3)

    @Column(length = 10)
    private String transhipment; // transhipment Allowed/Not Allowed // Varchar(10)
    
    private Integer shipmentPeriod; // ShipmentPeriod // Number

    private Date shipmentDateFirst; // date d'expedition // date

    private Date shipmentDateLast; // last date expedition // date


    public DealGoods() {
    }

    public DealGoods(Integer id, DealLC dealLC, String stepID, Integer seq, String goodsCODE,
                     String goodsType, String goodsDesc, String goodsOrigin,
                     String placeOfTakingCharge, String portOfLoading, String portOfDischarge,
                     String placeOfFinalDestination, String transportationType,
                     String transhipment, Integer shipmentPeriod, Date shipmentDateFirst,
                     Date shipmentDateLast) {
        this.id = id;
        this.dealLC = dealLC;
        this.stepID = stepID;
        this.seq = seq;
        this.goodsCODE = goodsCODE;
        this.goodsType = goodsType;
        this.goodsDesc = goodsDesc;
        this.goodsOrigin = goodsOrigin;
        this.placeOfTakingCharge = placeOfTakingCharge;
        this.portOfLoading = portOfLoading;
        this.portOfDischarge = portOfDischarge;
        this.placeOfFinalDestination = placeOfFinalDestination;
        this.transportationType = transportationType;
        this.transhipment = transhipment;
        this.shipmentPeriod = shipmentPeriod;
        this.shipmentDateFirst = shipmentDateFirst;
        this.shipmentDateLast = shipmentDateLast;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DealLC getDealLC() {
        return dealLC;
    }

    public void setDealLC(DealLC dealLC) {
        this.dealLC = dealLC;
    }

    public String getStepID() {
        return stepID;
    }

    public void setStepID(String stepID) {
        this.stepID = stepID;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getGoodsCODE() {
        return goodsCODE;
    }

    public void setGoodsCODE(String goodsCODE) {
        this.goodsCODE = goodsCODE;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public String getGoodsOrigin() {
        return goodsOrigin;
    }

    public void setGoodsOrigin(String goodsOrigin) {
        this.goodsOrigin = goodsOrigin;
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

    public String getTransportationType() {
        return transportationType;
    }

    public void setTransportationType(String transportationType) {
        this.transportationType = transportationType;
    }

    public String getTranshipment() {
        return transhipment;
    }

    public void setTranshipment(String transhipment) {
        this.transhipment = transhipment;
    }

    public Integer getShipmentPeriod() {
        return shipmentPeriod;
    }

    public void setShipmentPeriod(Integer shipmentPeriod) {
        this.shipmentPeriod = shipmentPeriod;
    }

    public Date getShipmentDateFirst() {
        return shipmentDateFirst;
    }

    public void setShipmentDateFirst(Date shipmentDateFirst) {
        this.shipmentDateFirst = shipmentDateFirst;
    }

    public Date getShipmentDateLast() {
        return shipmentDateLast;
    }

    public void setShipmentDateLast(Date shipmentDateLast) {
        this.shipmentDateLast = shipmentDateLast;
    }


}
