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

const EXPORT_MT = gql`
  mutation ExportMT($id: Int!, $mt: String) {
    exportMT(id: $id, mt: $mt)
  }
`;

const EXPORT_MT798 = gql`
  mutation ExportMT798($id: Int!, $mt: String) {
    exportMT798(dealId: $id, mt: $mt)
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



export { GET_DEALS, EXPORT_DEAL, CREATE_DEAL_LC, EXPORT_MT, EXPORT_MT798 };