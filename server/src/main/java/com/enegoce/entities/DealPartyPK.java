package com.enegoce.entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Embeddable
public class DealPartyPK implements Serializable {

    @ManyToOne
    @JoinColumn(name = "deal_id")
    private InfoDeal deal;

    @Column(length = 35)
    private String codPrt;

    private Long seq;

    public DealPartyPK() {
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

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public DealPartyPK(InfoDeal deal, String codPrt, Long seq) {
        this.deal = deal;
        this.codPrt = codPrt;
        this.seq = seq;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DealPartyPK)) {
            return false;
        }
        DealPartyPK castOther = (DealPartyPK) other;
        return this.deal.equals(castOther.deal)
                && this.codPrt.equals(castOther.codPrt)
                && this.seq.equals(castOther.seq);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.deal.hashCode();
        hash = hash * prime + this.codPrt.hashCode();
        hash = hash * prime + this.seq.hashCode();
        return hash;
    }
}
