import { Injectable } from '@angular/core';
import { Apollo } from 'apollo-angular';
import { Observable, map } from 'rxjs';
import { MtFieldMapping, MtFieldMappingInput } from '../../graphql/types';
import {
  GET_MAPPINGS, FILTER_MAPPINGS, ADD_MT_MAPPING, UPDATE_MT_MAPPING,
  DELETE_MT_MAPPING, MAPPINGS_BY_MT, MTS, MAPPINGS_BY_ST,
  FIELD_BY_ENTITY, GET_MAPPING_BY_ID,
} from '../../graphql/graphql.queries_mapping';

@Injectable({
  providedIn: 'root'
})
export class MappingService {

  constructor(private apollo: Apollo) { }

  getAllMappings(): Observable<MtFieldMapping[]> {
    return this.apollo.watchQuery<any>({
      query: GET_MAPPINGS
    }).valueChanges.pipe(
      map(result => result.data.getAllMappings)
    );
  }

  getMappingById(id: Number): Observable<MtFieldMapping> {
    return this.apollo.watchQuery<any>({
      query: GET_MAPPING_BY_ID,
      variables: {
        id: id
      }
    }).valueChanges.pipe(
      map(result => result.data.getMappingById)
    );
  }

  getUniqueMts(): Observable<string[]> {
    return this.apollo.watchQuery<any>({
      query: MTS
    }).valueChanges.pipe(
      map(result => result.data.mts)
    );
  }

  getFieldByEntity(entityName: String): Observable<string[]> {
    return this.apollo.watchQuery<any>({
      query: FIELD_BY_ENTITY,
      variables: {
        entityName: entityName
      }
    }).valueChanges.pipe(
      map(result => result.data.getFieldsForEntity)
    );
  }

  addMtFieldMapping(mapping: MtFieldMappingInput): Observable<MtFieldMapping> {
    return this.apollo.mutate<any>({
      mutation: ADD_MT_MAPPING,
      variables: {
        input: mapping
      }
    }).pipe(
      map(result => result.data.addMtFieldMapping)
    );
  }

  updateFieldMapping(id: Number, mapping: MtFieldMappingInput): Observable<MtFieldMapping> {
    return this.apollo.mutate<any>({
      mutation: UPDATE_MT_MAPPING,
      variables: {
        id: id,
        input: mapping
      }
    }).pipe(
      map(result => result.data.updateFieldMapping)
    );
  }

  deleteFieldMapping(id: Number): Observable<boolean> {
    return this.apollo.mutate<{ deleteFieldMapping: boolean }>({
      mutation: DELETE_MT_MAPPING,
      variables: { id },
      refetchQueries: [{ query: GET_MAPPINGS }]
    }).pipe(
      map(result => result.data?.deleteFieldMapping ?? false)
    );
  }

  ////////////////// FILTERING ////////////////
  /////////////////////////////////////////////

  MappingsByMT(mt: String): Observable<MtFieldMapping[]> {
    return this.apollo.watchQuery<any>({
      query: MAPPINGS_BY_MT,
      variables: {
        mt: mt
      }
    }).valueChanges.pipe(
      map(result => result.data.mappingsByMt)
    );
  }

  MappingsByST(status: String): Observable<MtFieldMapping[]> {
    return this.apollo.watchQuery<any>({
      query: MAPPINGS_BY_ST,
      variables: {
        status: status
      }
    }).valueChanges.pipe(
      map(result => result.data.mappingsByST)
    );
  }

  findByFilter(filter: String): Observable<MtFieldMapping[]> {
    return this.apollo.watchQuery<any>({
      query: FILTER_MAPPINGS,
      variables: {
        filter: filter
      }
    }).valueChanges.pipe(
      map(result => result.data.findByFilter)
    );
  }

}
