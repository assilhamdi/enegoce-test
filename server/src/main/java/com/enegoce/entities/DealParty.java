package com.enegoce.entities;

import jakarta.persistence.*;

@Entity
public class DealParty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "deal_id")
    private InfoDeal deal;

    @Column(length = 35)
    private String codPrt;

    @Column(length = 35)
    private String name;

    @Column(length = 35)
    private String address;

    @Column(length = 35)
    private String address1;

    @Column(length = 35)
    private String address2;

    @Column(length = 35)
    private String city;

    @Column(length = 10)
    private String cp; //TODO: party code ??

    @Column(length = 35)
    private String country;

    public DealParty() {
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

    public String getCodPrt() {
        return codPrt;
    }

    public void setCodPrt(String codPrt) {
        this.codPrt = codPrt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
}
