package com.enegoce.entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Embeddable
public class DealGoodsPK implements Serializable {

    @ManyToOne
    @JoinColumn(name = "deal_id")
    private InfoDeal deal;

    @Column(length = 5)
    private String stepId;

    @Column(length = 3)
    private Long  seq;

    public DealGoodsPK() {
    }

    public InfoDeal getDeal() {
        return deal;
    }

    public void setDeal(InfoDeal deal) {
        this.deal = deal;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public DealGoodsPK(InfoDeal deal, String stepId, Long seq) {
        this.deal = deal;
        this.stepId = stepId;
        this.seq = seq;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DealGoodsPK)) {
            return false;
        }
        DealGoodsPK castOther = (DealGoodsPK) other;
        return this.deal.equals(castOther.deal)
                && this.stepId.equals(castOther.stepId)
                && this.seq.equals(castOther.seq);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.deal.hashCode();
        hash = hash * prime + this.stepId.hashCode();
        hash = hash * prime + this.seq.hashCode();
        return hash;
    }
}
