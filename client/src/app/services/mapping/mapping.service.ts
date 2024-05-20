import { Injectable } from '@angular/core';
import { Apollo } from 'apollo-angular';
import { Observable, map } from 'rxjs';
import { MtFieldMapping, MtFieldMappingInput } from '../../graphql/types';
import { GET_MAPPINGS, SORT_MAPPINGS_BY_ORDER, ADD_MT_MAPPING, UPDATE_MT_MAPPING } from '../../graphql/graphql.queries_mapping';

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

  orderMappingsByFO(order: boolean): Observable<MtFieldMapping[]> {
    return this.apollo.watchQuery<any>({
      query: SORT_MAPPINGS_BY_ORDER,
      variables: {
        order: order
      }
    }).valueChanges.pipe(
      map(result => result.data.orderMappingsByFO)
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

  updateMtFieldMapping(id: Number, mapping: MtFieldMappingInput): Observable<MtFieldMapping> {
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

}
