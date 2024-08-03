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
    id: number
    status: string
    databaseField: string
    tag: string
    fieldDescription: string
    entityName: string
    mappingRule: string
    mt: string
    fieldOrder: number
}

export type MtFieldMappingInput = {
    status: string;
    tag: string;
    fieldDescription: string;
    databaseField: string;
    entityName: string;
    mt: string;
    fieldOrder: number;
    fields: string[];
    delimiter: string;
    code: string; 
}