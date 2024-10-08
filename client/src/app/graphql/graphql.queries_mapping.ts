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
        fieldName
        mappingRule
        mt
        fieldOrder
    }
  }
`;

const GET_MAPPING_BY_ID = gql`
  query GetMappingById($id: Int!) {
    getMappingById(id: $id) {
      id
      status
      databaseField
      tag
      fieldDescription
      entityName
      fieldName
      mappingRule
      mt
      fieldOrder
    }
  }
`;

const ADD_MT_MAPPING = gql`
  mutation AddMtMapping($input: MtFieldMappingInput!) {
    addMtFieldMapping(input: $input) {
      status
      tag
      fieldName
      fieldDescription
      databaseField
      entityName
      mt
      fieldOrder
      mappingRule
    }
  }
`;

const UPDATE_MT_MAPPING = gql`
  mutation UpdateMtMapping($id: Int!, $input: MtFieldMappingInput!) {
    updateFieldMapping(id: $id, input: $input) {
      status
      tag
      fieldName
      fieldDescription
      databaseField
      entityName
      mt
      fieldOrder
      mappingRule
    }
  }
`;

const DELETE_MT_MAPPING = gql`
  mutation DeleteMtMapping($id: Int!) {
    deleteFieldMapping(id: $id)
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
      fieldName
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
      fieldName
      mappingRule
      mt
      fieldOrder
    }
  }
`;

const FILTER_MAPPINGS = gql`
  query FindByFilter($filter: String!) {
    findByFilter(filter: $filter) {
      id
      status
      databaseField
      tag
      fieldDescription
      entityName
      fieldName
      mappingRule
      mt
      fieldOrder
    }
  }
`;

export {
  GET_MAPPINGS, ADD_MT_MAPPING, FILTER_MAPPINGS,
  UPDATE_MT_MAPPING, DELETE_MT_MAPPING, MAPPINGS_BY_MT, MTS,
  MAPPINGS_BY_ST, FIELD_BY_ENTITY, GET_MAPPING_BY_ID
}