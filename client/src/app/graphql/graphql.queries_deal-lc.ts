import { gql } from "apollo-angular";

const GET_DEALS = gql`
  query {
    getAllInfoDeals {
      id
      formLC
      dueDate
      expiryDate
      expiryPlace
      bankISSRef
      currencyID
      lcAmount
      varAmountTolerance
      partialTranshipment
      transhipment
      presDay
      confirmationCharge
      addAmtCovered
    }
  }
`;

export { GET_DEALS };