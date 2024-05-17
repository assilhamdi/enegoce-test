import { gql } from "apollo-angular";

const GET_DEALS = gql`
  query {
    getAllDealLCs {
      dealId
      formLC
      dueDate
      expiryDate
      expiryPlace
      customerReference
      counterParty
      bankISSRef
      bankRMBRef
      creationDate
      currencyId
      lcAmount
      varAmountTolerance
      availableWith
      partialTranshipment
      transhipment
    }
  }
`;

const EXPORT_DEAL = gql`
  mutation ExportDeal($id: Int!) {
    exportDeal(id: $id)
  }
`;

const EXPORT_MT700 = gql`
  mutation ExportMT700($id: Int!) {
    exportMT700(id: $id)
  }
`;

const CREATE_DEAL_LC = gql`
  mutation CreateDealLC($input: DealLCInput!) {
    addDealLC(input: $input) {
      formLC
      dueDate
      expiryDate
      expiryPlace
      customerReference
      counterParty
      bankISSRef
      bankRMBRef
      creationDate
      currencyId
      lcAmount
      varAmountTolerance
      availableWith
      partialTranshipment
      transhipment
    }
  }
`;



export {GET_DEALS, EXPORT_DEAL, CREATE_DEAL_LC, EXPORT_MT700};