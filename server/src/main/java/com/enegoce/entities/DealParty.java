package com.enegoce.entities;

import jakarta.persistence.*;

@Entity
public class DealParty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "dealLcId")
    private DealLC dealLC = new DealLC();

    @Column(name = "codPrt", length = 35)
    private String codPrt;

    @Column(name = "name", length = 35)
    private String name;

    @Column(name = "address", length = 35)
    private String address;

    @Column(name = "address1", length = 35)
    private String address1;

    @Column(name = "address2", length = 35)
    private String address2;

    @Column(name = "city", length = 35)
    private String city;

    @Column(name = "cp", length = 10)
    private String cp;

    @Column(name = "country", length = 35)
    private String country;

    public DealParty() {
    }

    public DealParty(Integer id, DealLC dealLC, String codPrt, String name, String address,
                     String address1, String address2, String city, String cp,
                     String country) {
        this.id = id;
        this.dealLC = dealLC;
        this.codPrt = codPrt;
        this.name = name;
        this.address = address;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.cp = cp;
        this.country = country;
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
