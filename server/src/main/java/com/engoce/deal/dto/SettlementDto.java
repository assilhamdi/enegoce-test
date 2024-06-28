package com.engoce.deal.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SettlementDto {

    private Long id;
    private String availableWithBank;
    private String availableWithOther;
    private String mixedPay1;
    private String mixedPay2;
    private String mixedPay3;
    private String mixedPay4;
    private String negDefPay1;
    private String negDefPay2;
    private String negDefPay3;
    private String negDefPay4;
    private Long idDeal;

}
