package com.enegoce.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
public class DealComment implements Serializable {

    @EmbeddedId
    private DealCommentPK id;

    @Column(length = 6500)
    private String comment;

    private LocalDate dateCreation;

    @Column(length = 35)
    private String useName;

    @Column(length = 5)
    private String typeComt;

    private String stepId;

    public DealComment() {
    }

    public DealCommentPK getId() {
        return id;
    }

    public void setId(DealCommentPK id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getUseName() {
        return useName;
    }

    public void setUseName(String useName) {
        this.useName = useName;
    }

    public String getTypeComt() {
        return typeComt;
    }

    public void setTypeComt(String typeComt) {
        this.typeComt = typeComt;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public DealComment(DealCommentPK id, String comment, LocalDate dateCreation, String useName, String typeComt, String stepId) {
        this.id = id;
        this.comment = comment;
        this.dateCreation = dateCreation;
        this.useName = useName;
        this.typeComt = typeComt;
        this.stepId = stepId;
    }
}
