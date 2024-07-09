package com.enegoce.entities;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Transport implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String incoterm;

    private String partialShipment;

    private String transhipment;

    private String transportType;

    private String trackingType;

    @Column
    private String placeOfTakingCharge;

    @Column
    private String portOfLoading;

    @Column
    private String portOfDischarge;

    @Column
    private String placeOfFinalDestination;

    private LocalDate latestDateOfShipment;

    @Column
    private String shipmentPeriod;

    @ManyToOne
    private InfoDeal dealTransport;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIncoterm() {
        return incoterm;
    }

    public void setIncoterm(String incoterm) {
        this.incoterm = incoterm;
    }

    public String getPartialShipment() {
        return partialShipment;
    }

    public void setPartialShipment(String partialShipment) {
        this.partialShipment = partialShipment;
    }

    public String getTranshipment() {
        return transhipment;
    }

    public void setTranshipment(String transhipment) {
        this.transhipment = transhipment;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getTrackingType() {
        return trackingType;
    }

    public void setTrackingType(String trackingType) {
        this.trackingType = trackingType;
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

    public LocalDate getLatestDateOfShipment() {
        return latestDateOfShipment;
    }

    public void setLatestDateOfShipment(LocalDate latestDateOfShipment) {
        this.latestDateOfShipment = latestDateOfShipment;
    }

    public String getShipmentPeriod() {
        return shipmentPeriod;
    }

    public void setShipmentPeriod(String shipmentPeriod) {
        this.shipmentPeriod = shipmentPeriod;
    }

    public InfoDeal getDealTransport() {
        return dealTransport;
    }

	public void setDealTransport(InfoDeal dealTransport) {
		this.dealTransport = dealTransport;
	}


    
}
