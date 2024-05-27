import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { DealLC, DealLCInput } from '../../graphql/types';
import { CREATE_DEAL_LC, EXPORT_DEAL, GET_DEALS, EXPORT_MT, EXPORT_MT798 } from '../../graphql/graphql.queries_deal-lc';
import { Apollo } from 'apollo-angular';

@Injectable({
  providedIn: 'root'
})
export class DealLcService {

  constructor(private apollo: Apollo) { }

  getAllDealLCs(): Observable<DealLC[]> {
    return this.apollo.watchQuery<any>({
      query: GET_DEALS
    }).valueChanges.pipe(
      map(result => result.data.getAllDealLCs)
    );
  }

  exportDeal(dealId: Number): Observable<any> {
    return this.apollo.mutate({
      mutation: EXPORT_DEAL,
      variables: {
        id: dealId
      }
    });
  }

  exportMT(dealId: Number, mt: String): Observable<any> {
    return this.apollo.mutate({
      mutation: EXPORT_MT,
      variables: {
        id: dealId,
        mt: mt
      }
    })
  }

  exportMT798(id: Number, mt: String): Observable<any> {
    return this.apollo.mutate({
      mutation: EXPORT_MT798,
      variables: {
        id: id,
        mt: mt
      }
    })
  }

  addDealLC(deal: DealLCInput): Observable<DealLC> {
    return this.apollo.mutate<any>({
      mutation: CREATE_DEAL_LC,
      variables: {
        input: deal
      }
    }).pipe(
      map(result => result.data.addDealLC)
    );
  }

}
