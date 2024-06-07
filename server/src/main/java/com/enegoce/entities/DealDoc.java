package com.enegoce.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class DealDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "deal_id")
    private InfoDeal deal;

    @Column(length = 100)
    private String idGED;

    @Column(length = 400)
    private String description;

    @Column(length = 1)
    private String sentID;

    private Date dateUpload;

    private String userUpload;

    public DealDoc() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public InfoDeal getDeal() {
        return deal;
    }

    public void setDeal(InfoDeal deal) {
        this.deal = deal;
    }

    public String getIdGED() {
        return idGED;
    }

    public void setIdGED(String idGED) {
        this.idGED = idGED;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSentID() {
        return sentID;
    }

    public void setSentID(String sentID) {
        this.sentID = sentID;
    }

    public Date getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(Date dateUpload) {
        this.dateUpload = dateUpload;
    }

    public String getUserUpload() {
        return userUpload;
    }

    public void setUserUpload(String userUpload) {
        this.userUpload = userUpload;
    }
}
