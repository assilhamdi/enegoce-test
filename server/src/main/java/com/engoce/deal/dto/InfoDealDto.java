package com.engoce.deal.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Setter
@Getter
public class InfoDealDto {

    private Long id; // Documentary Credit Number
    private String formLC; // 40A
    private LocalDate dueDate; // 31C - Date of Issue
    private LocalDate expiryDate; // 31D - Date of Expiry
    private String expiryPlace; // 31D
    private String bankISSRef; // Sender - 51a
    private String currencyID; // 32B - Currency Code
    private BigDecimal lcAmount; // 32B - Amount
    private BigDecimal varAmountTolerance; // 39A - Percentage Credit Amt Tolerance
    private String partialTranshipment; // 43P - Partial Shipments
    private String transhipment; // 43T - Partial Transhipment
    private Integer presDay; // 48
    private String confirmationCharge; // 49
    private String addAmtCovered; // 39C
    private String draftAt; // 42C
    private String draft; // 42a
    private String document; // 46A

}

