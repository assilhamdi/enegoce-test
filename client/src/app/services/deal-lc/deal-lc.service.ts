import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { InfoDeal } from '../../graphql/types';
import { GET_DEALS } from '../../graphql/graphql.queries_deal-lc';
import { Apollo } from 'apollo-angular';

@Injectable({
  providedIn: 'root'
})
export class DealLcService {

  constructor(private apollo: Apollo) { }

  getAllInfoDeals(): Observable<InfoDeal[]> {
    return this.apollo.watchQuery<any>({
      query: GET_DEALS
    }).valueChanges.pipe(
      map(result => result.data.getAllInfoDeals)
    );
  }

}
