package com.enegoce.entities;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
public class DealComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "dealLcId")
    private DealLC dealLC;

    @Column(length = 350)
    private String comment;

    private Integer seq;

    private Timestamp dateCreation;

    /*@Column(name = "user", length = 35)
    private String user;*/

    public DealComment() {
    }

    public DealComment(Integer id, DealLC dealLC, String comment, Integer seq, Timestamp dateCreation) {
        this.id = id;
        this.dealLC = dealLC;
        this.comment = comment;
        this.seq = seq;
        this.dateCreation = dateCreation;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }
}
