package com.enegoce.entities;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
public class DealStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "dealLcId")
    private DealLC dealLC;

    @Column(name = "Seq")
    private Integer seq;

    @Column(name = "DealID")
    private Long dealID;

    @Column(name = "Step", length = 3)
    private String step;

    @Column(name = "version", length = 3)
    private String version;

    @Column(name = "comment")
    private String comment;

    /*@Column(name = "user")
    private String user;*/

    @Column(name = "dateCreation")
    private Timestamp dateCreation;

    public DealStep() {
    }

    public DealStep(Integer id, DealLC dealLC, Integer seq, Long dealID,
                    String step, String version, String comment, Timestamp dateCreation) {
        this.id = id;
        this.dealLC = dealLC;
        this.seq = seq;
        this.dealID = dealID;
        this.step = step;
        this.version = version;
        this.comment = comment;
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

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Long getDealID() {
        return dealID;
    }

    public void setDealID(Long dealID) {
        this.dealID = dealID;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }
}
