package com.enegoce.entities;

public record DealLCInput(String formLC, String dueDate, String expiryDate, String expiryPlace,
                          String customerReference, String counterParty, String bankISSRef,
                          String bankRMBRef, String creationDate, String currencyId,
                          String lcAmount, String varAmountTolerance, String availableWith,
                          String partialTranshipment, String transhipment, Integer presDay,
                          String confirmationCharge, String addAmtCovered) {
}
