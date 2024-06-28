package com.enegoce.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DealGoods implements Serializable {

    @EmbeddedId
    private DealGoodsPK id;

    private String productCode;

    private String productName;

    private String productOrigine;

    @Column(length = 6500)
    private String goodsDesc;

    @Column(precision = 13, scale = 2)
    private BigDecimal unitPrice;

    @Column(length = 20)
    private int quantity;

    @Column(precision = 13, scale = 2)
    private BigDecimal totalAmount;

    @Column(length = 50)
    private String placeOfTakingCharge;

    @Column(length = 50)
    private String portOfLoading;

    @Column(length = 50)
    private String portOfDischarge;

    @Column(length = 50)
    private String placeOfFinalDestination;

    private Date shipmentDateLast;

    private Integer shipmentPeriod;
}
