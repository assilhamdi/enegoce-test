import { gql } from "apollo-angular";

const GET_MAPPINGS = gql`
  query {
    getAllMappings {
        id
        status
        databaseField
        tag
        fieldDescription
        entityName
        mappingRule
        mt
        fieldOrder
    }
  }
`;


const SORT_MAPPINGS_BY_ORDER = gql`
  query OrderMappingsByFO($order: Boolean!) {
    orderMappingsByFO(order: $order) {
      id
      status
      databaseField
      tag
      fieldDescription
      entityName
      mappingRule
      mt
      fieldOrder
    }
  }
`;

const ADD_MT_MAPPING = gql`
  mutation CreateMtMapping($input: MtFieldMappingInput!) {
    addMtFieldMapping(input: $input) {
        status
        databaseField
        tag
        fieldDescription
        entityName
        mappingRule
        mt
        fieldOrder
    }
  }
`;

const UPDATE_MT_MAPPING = gql`
  mutation UpdateMtMapping($id: Int!, $input: MtFieldMappingInput!) {
    updateFieldMapping(id: $id, input: $input) {
      status
      databaseField
      tag
      fieldDescription
      entityName
      mappingRule
      mt
      fieldOrder
    }
  }
`;

const DELETE_MT_MAPPING = gql`
  mutation DeleteMtMapping($id: Int!) {
    deleteFieldMapping(id: $id)
  }
`;

export { GET_MAPPINGS, ADD_MT_MAPPING, SORT_MAPPINGS_BY_ORDER, UPDATE_MT_MAPPING, DELETE_MT_MAPPING }