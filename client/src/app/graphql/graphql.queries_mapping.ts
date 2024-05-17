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

export {GET_MAPPINGS, ADD_MT_MAPPING}