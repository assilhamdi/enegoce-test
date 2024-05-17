import { Injectable } from '@angular/core';
import { Apollo } from 'apollo-angular';
import { Observable, map } from 'rxjs';
import { MtFieldMapping } from '../../graphql/types';
import { GET_MAPPINGS } from '../../graphql/graphql.queries_mapping';

@Injectable({
  providedIn: 'root'
})
export class MappingService {

  constructor(private apollo:Apollo) {}

  getAllMappings(): Observable<MtFieldMapping[]> {
    return this.apollo.watchQuery<any>({
      query: GET_MAPPINGS
    }).valueChanges.pipe(
      map(result => result.data.getAllMappings)
    );
  }
}
