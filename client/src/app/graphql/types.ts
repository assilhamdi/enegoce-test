export type DealLC={
    dealId: Number
    formLC: String
    dueDate: String
    expiryDate: String
    expiryPlace: String
    customerReference: String
    counterParty: String
    bankISSRef: String
    bankRMBRef: String
    creationDate: String
    currencyId: String
    lcAmount: String
    varAmountTolerance: String
    availableWith: String
    partialTranshipment: String
    transhipment: String
}

export type DealLCInput={
    formLC: String
    dueDate: String
    expiryDate: String
    expiryPlace: String
    customerReference: String
    counterParty: String
    bankISSRef: String
    bankRMBRef: String
    creationDate: String
    currencyId: String
    lcAmount: String
    varAmountTolerance: String
    availableWith: String
    partialTranshipment: String
    transhipment: String
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
    mappingRule: String
    databaseField: String
    entityName: String
    mt: String
    fieldOrder: Number
}