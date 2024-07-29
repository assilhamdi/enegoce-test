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

const MTS = gql`
  query {
    mts
  }
`;

const FIELD_BY_ENTITY = gql`
  query GetFieldsForEntity($entityName: String!){
    getFieldsForEntity (entityName: $entityName)    
  }
`;

/////////////////// SORTING /////////////////
/////////////////////////////////////////////

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

////////////////// FILTERING ////////////////
/////////////////////////////////////////////

const MAPPINGS_BY_MT = gql`
  query MappingsByMT($mt: String!) {
    mappingsByMt(mt: $mt) {
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

const MAPPINGS_BY_ST = gql`
  query MappingsByStatus($status: String!) {
    mappingsByST(status: $status) {
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

const MAPPINGS_BY_FD = gql`
  query MappingsByDbField($dbField: String!) {
    mappingsByDF(dbField: $dbField) {
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

export { GET_MAPPINGS, ADD_MT_MAPPING, SORT_MAPPINGS_BY_ORDER, UPDATE_MT_MAPPING, DELETE_MT_MAPPING, MAPPINGS_BY_MT, MTS, MAPPINGS_BY_FD, MAPPINGS_BY_ST, FIELD_BY_ENTITY }