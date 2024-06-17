package com.enegoce.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

@Embeddable
public class DealCommentPK implements Serializable {

    @ManyToOne
    @JoinColumn(name = "deal_id")
    private InfoDeal deal;

    private Long seq;

    public DealCommentPK() {
    }

    public InfoDeal getDeal() {
        return deal;
    }

    public void setDeal(InfoDeal deal) {
        this.deal = deal;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public DealCommentPK(InfoDeal deal, Long seq) {
        this.deal = deal;
        this.seq = seq;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DealCommentPK)) {
            return false;
        }
        DealCommentPK castOther = (DealCommentPK) other;
        return this.deal.equals(castOther.deal)
                && this.seq.equals(castOther.seq);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.deal.hashCode();
        hash = hash * prime + this.seq.hashCode();
        return hash;
    }

}
