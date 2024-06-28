package com.engoce.deal.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class DealGoodsDto {

    private DealGoodsPKID id;
    private String goodsDesc;
    private String placeOfTakingCharge;
    private String portOfLoading;
    private String portOfDischarge;
    private String placeOfFinalDestination;
    private Date shipmentDateLast;
    private Integer shipmentPeriod;

}
