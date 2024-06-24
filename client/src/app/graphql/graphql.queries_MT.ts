import { gql } from "apollo-angular";

const EXPORT_MT = gql`
  mutation ExportMT($id: Int!, $mt: String, $format: String) {
    exportMT(id: $id, mt: $mt, format: $format)
  }
`;

const EXPORT_MT798 = gql`
  mutation ExportMT798($id: Int!, $mt: String $format: String) {
    exportMT798(dealId: $id, mt: $mt, format: $format)
  }
`;

export { EXPORT_MT, EXPORT_MT798 };