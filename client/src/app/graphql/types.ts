export type InfoDeal={
    id: Number
    formLC: String
    dueDate: String
    expiryDate: String
    expiryPlace: String
    bankISSRef: String
    currencyID: String
    lcAmount: String
    varAmountTolerance: String
    partialTranshipment: String
    transhipment: String
    presDay: Number
    confirmationCharge: String
    addAmtCovered: String
}

export type MtFieldMapping ={
    id: Number
    status: String
    databaseField: String
    tag: String
    fieldDescription: String
    entityName: String
    mappingRule: String
    mt: String
    fieldOrder: Number
}

export type MtFieldMappingInput ={
    status: String
    tag: String
    fieldDescription: String
    databaseField: String
    entityName: String
    mt: String
    fieldOrder: Number
}