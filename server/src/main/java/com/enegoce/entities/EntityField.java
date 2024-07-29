package com.enegoce.entities;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public enum EntityField {

    // DealComment fields
    DEAL_COMMENT_COMMENT("DealComment", "comment"),
    DEAL_COMMENT_DATE_CREATION("DealComment", "dateCreation"),
    DEAL_COMMENT_USE_NAME("DealComment", "useName"),
    DEAL_COMMENT_TYPE_COMT("DealComment", "typeComt"),
    DEAL_COMMENT_STEP_ID("DealComment", "stepId"),

    // DealParty fields
    DEAL_PARTY_NOM("DealParty", "nom"),
    DEAL_PARTY_STREET1("DealParty", "street1"),
    DEAL_PARTY_STREET2("DealParty", "street2"),
    DEAL_PARTY_STREET3("DealParty", "street3"),
    DEAL_PARTY_TOWN("DealParty", "town"),
    DEAL_PARTY_CP("DealParty", "cp"),
    DEAL_PARTY_COUNTRY("DealParty", "country"),
    DEAL_PARTY_IBAN("DealParty", "iban"),
    DEAL_PARTY_DEV("DealParty", "dev"),
    DEAL_PARTY_DYNAMIC_DATA1("DealParty", "dynamicData1"),
    DEAL_PARTY_DYNAMIC_DATA2("DealParty", "dynamicData2"),
    DEAL_PARTY_DYNAMIC_DATA3("DealParty", "dynamicData3"),
    DEAL_PARTY_NIDN("DealParty", "nidn"),
    DEAL_PARTY_COMP_CHECK("DealParty", "compCheck"),
    DEAL_PARTY_USER_CREATED("DealParty", "userCreated"),
    DEAL_PARTY_DATE_CREATION("DealParty", "dateCreation"),
    DEAL_PARTY_USER_UPDATED("DealParty", "userUpdated"),
    DEAL_PARTY_DATE_UPDATED("DealParty", "dateUpdated"),
    DEAL_PARTY_STEP_ID("DealParty", "stepId"),

    // InfoDeal fields
    INFO_DEAL_ID("InfoDeal", "id"),
    INFO_DEAL_FORM_LC("InfoDeal", "formLC"),
    INFO_DEAL_DUE_DATE("InfoDeal", "dueDate"),
    INFO_DEAL_EXPIRY_DATE("InfoDeal", "expiryDate"),
    INFO_DEAL_EXPIRY_PLACE("InfoDeal", "expiryPlace"),
    INFO_DEAL_BANK_ISS_REF("InfoDeal", "bankISSRef"),
    INFO_DEAL_CURRENCY_ID("InfoDeal", "currencyID"),
    INFO_DEAL_LC_AMOUNT("InfoDeal", "lcAmount"),
    INFO_DEAL_VAR_AMOUNT_TOLERANCE("InfoDeal", "varAmountTolerance"),
    INFO_DEAL_PARTIAL_TRANSHIPMENT("InfoDeal", "partialTranshipment"),
    INFO_DEAL_TRANSHIPMENT("InfoDeal", "transhipment"),
    INFO_DEAL_PRES_DAY("InfoDeal", "presDay"),
    INFO_DEAL_CONFIRMATION_CHARGE("InfoDeal", "confirmationCharge"),
    INFO_DEAL_ADD_AMT_COVERED("InfoDeal", "addAmtCovered"),
    INFO_DEAL_DRAFT_AT("InfoDeal", "draftAt"),
    INFO_DEAL_DRAFT("InfoDeal", "draft"),
    INFO_DEAL_DOCUMENT("InfoDeal", "document"),

    // Settlement fields
    SETTLEMENT_ID("Settlement", "id"),
    SETTLEMENT_PAYMENT_TYPE("Settlement", "paymentType"),
    SETTLEMENT_AVAILABLE_WITH_BANK("Settlement", "availableWithBank"),
    SETTLEMENT_AVAILABLE_WITH_OTHER("Settlement", "availableWithOther"),
    SETTLEMENT_MIXED_PAY1("Settlement", "mixedPay1"),
    SETTLEMENT_MIXED_PAY2("Settlement", "mixedPay2"),
    SETTLEMENT_MIXED_PAY3("Settlement", "mixedPay3"),
    SETTLEMENT_MIXED_PAY4("Settlement", "mixedPay4"),
    SETTLEMENT_NEG_DEF_PAY1("Settlement", "negDefPay1"),
    SETTLEMENT_NEG_DEF_PAY2("Settlement", "negDefPay2"),
    SETTLEMENT_NEG_DEF_PAY3("Settlement", "negDefPay3"),
    SETTLEMENT_NEG_DEF_PAY4("Settlement", "negDefPay4"),
    SETTLEMENT_DEAL("Settlement", "deal"),

    // Transport fields
    TRANSPORT_ID("Transport", "id"),
    TRANSPORT_INCOTERM("Transport", "incoterm"),
    TRANSPORT_PARTIAL_SHIPMENT("Transport", "partialShipment"),
    TRANSPORT_TRANSHIPMENT("Transport", "transhipment"),
    TRANSPORT_TRANSPORT_TYPE("Transport", "transportType"),
    TRANSPORT_TRACKING_TYPE("Transport", "trackingType"),
    TRANSPORT_PLACE_OF_TAKING_CHARGE("Transport", "placeOfTakingCharge"),
    TRANSPORT_PORT_OF_LOADING("Transport", "portOfLoading"),
    TRANSPORT_PORT_OF_DISCHARGE("Transport", "portOfDischarge"),
    TRANSPORT_PLACE_OF_FINAL_DESTINATION("Transport", "placeOfFinalDestination"),
    TRANSPORT_LATEST_DATE_OF_SHIPMENT("Transport", "latestDateOfShipment"),
    TRANSPORT_SHIPMENT_PERIOD("Transport", "shipmentPeriod"),
    TRANSPORT_DEAL_TRANSPORT("Transport", "dealTransport");

    private final String entity;
    private final String field;

    EntityField(String entity, String field) {
        this.entity = entity;
        this.field = field;
    }

    public String getEntity() {
        return entity;
    }

    public String getField() {
        return field;
    }
}
