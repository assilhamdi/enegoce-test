import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EXPORT_MT, EXPORT_MT798 } from '../../graphql/graphql.queries_MT';
import { Apollo } from 'apollo-angular';

@Injectable({
  providedIn: 'root'
})
export class MTService {

  constructor(private apollo: Apollo) { }

  exportMT(dealId: Number, mt: String, format: String): Observable<any> {
    return this.apollo.mutate({
      mutation: EXPORT_MT,
      variables: {
        id: dealId,
        mt: mt,
        format: format
      }
    })
  }

  exportMT798(id: Number, mt: String, format: String): Observable<any> {
    return this.apollo.mutate({
      mutation: EXPORT_MT798,
      variables: {
        id: id,
        mt: mt,
        format: format
      }
    })
  }
}
