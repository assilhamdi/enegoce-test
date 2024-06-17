package com.enegoce.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class DealParty implements Serializable {

    @EmbeddedId
    private DealPartyPK id;

    @Column(length = 35)
    private String nom;
    
    @Column(length = 105)
    private String street1;
    
    @Column(length = 105)
    private String street2;
    
    @Column(length = 105)
    private String street3;
    
    @Column(length = 35)
    private String town;
    
    @Column(length = 35)
    private String cp;
    
    @Column(length = 2)
    private String country;
    
    @Column(length = 35)
    private String iban;
    
    @Column(length = 35)
    private String dev;
    
    @Column(length = 35)
    private String dynamicData1;
    
    @Column(length = 35)
    private String dynamicData2;
    
    @Column(length = 35)
    private String dynamicData3;
    
    @Column(length = 35)
    private String nidn;
    
    @Column(length = 1)
    private String compCheck;
    
    @Column(length = 35)
    private String userCreated;
    
    private LocalDate dateCreation;
    
    @Column(length = 35)
    private String userUpdated;
    
    @Column(length = 35)
    private LocalDate dateUpdated;
    
    @Column(length = 5)
    private String stepId;

    public DealParty() {
    }

    public DealPartyPK getId() {
        return id;
    }

    public void setId(DealPartyPK id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getStreet3() {
        return street3;
    }

    public void setStreet3(String street3) {
        this.street3 = street3;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getDev() {
        return dev;
    }

    public void setDev(String dev) {
        this.dev = dev;
    }

    public String getDynamicData1() {
        return dynamicData1;
    }

    public void setDynamicData1(String dynamicData1) {
        this.dynamicData1 = dynamicData1;
    }

    public String getDynamicData2() {
        return dynamicData2;
    }

    public void setDynamicData2(String dynamicData2) {
        this.dynamicData2 = dynamicData2;
    }

    public String getDynamicData3() {
        return dynamicData3;
    }

    public void setDynamicData3(String dynamicData3) {
        this.dynamicData3 = dynamicData3;
    }

    public String getNidn() {
        return nidn;
    }

    public void setNidn(String nidn) {
        this.nidn = nidn;
    }

    public String getCompCheck() {
        return compCheck;
    }

    public void setCompCheck(String compCheck) {
        this.compCheck = compCheck;
    }

    public String getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(String userCreated) {
        this.userCreated = userCreated;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getUserUpdated() {
        return userUpdated;
    }

    public void setUserUpdated(String userUpdated) {
        this.userUpdated = userUpdated;
    }

    public LocalDate getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDate dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public DealParty(DealPartyPK id, String nom, String street1, String street2, String street3, String town, String cp, String country, String iban, String dev, String dynamicData1, String dynamicData2, String dynamicData3, String nidn, String compCheck, String userCreated, LocalDate dateCreation, String userUpdated, LocalDate dateUpdated, String stepId) {
        this.id = id;
        this.nom = nom;
        this.street1 = street1;
        this.street2 = street2;
        this.street3 = street3;
        this.town = town;
        this.cp = cp;
        this.country = country;
        this.iban = iban;
        this.dev = dev;
        this.dynamicData1 = dynamicData1;
        this.dynamicData2 = dynamicData2;
        this.dynamicData3 = dynamicData3;
        this.nidn = nidn;
        this.compCheck = compCheck;
        this.userCreated = userCreated;
        this.dateCreation = dateCreation;
        this.userUpdated = userUpdated;
        this.dateUpdated = dateUpdated;
        this.stepId = stepId;
    }
}
