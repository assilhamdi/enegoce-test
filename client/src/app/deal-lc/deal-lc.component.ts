import { Component, OnInit } from '@angular/core';
import { InfoDeal } from '../graphql/types';
import { DealLcService } from '../services/deal-lc/deal-lc.service';
import { catchError } from 'rxjs/operators';
import { EMPTY } from 'rxjs';
import { MappingService } from '../services/mapping/mapping.service';
import { MTService } from '../services/MT/mt.service';

@Component({
  selector: 'app-deal-lc',
  templateUrl: './deal-lc.component.html',
  styleUrl: './deal-lc.component.css'
})
export class DealLcComponent implements OnInit {

  deals: InfoDeal[] = [];
  distinctMtValues: string[] = [];
  formats: string[] = ["XML","TXT"]
  format: string = "";
  selectedMt: string = "";
  selectedSubMt: string = "";

  constructor(private dealService: DealLcService, private mappingService: MappingService, private mtService: MTService) { }

  ngOnInit(): void {
    this.fetchDeals();
    this.fetchUniqueMts();
  }

  fetchDeals() {
    this.dealService.getAllInfoDeals().subscribe(deals => { this.deals = deals; });
  }

  fetchUniqueMts(): void {
    this.mappingService.getUniqueMts().subscribe(
      mts => {
        this.distinctMtValues = mts;
        console.log('Distinct MT values:', this.distinctMtValues);
      },
      error => {
        console.error('Error fetching unique MTs:', error);
      }
    );
  }

  exportMT(dealId: Number, mt: String, format:String) {
    this.mtService.exportMT(dealId, mt, format)
      .pipe(
        catchError(error => {
          console.error('Error exporting MT', mt, ':', error);
          return EMPTY; // Return empty observable to handle the error
        })
      )
      .subscribe(result => {
        console.log('Deal exported successfully:', result);
      });
  }

  exportMT798(id: Number, mt: String, format: String) {
    this.mtService.exportMT798(id, mt, format)
      .pipe(
        catchError(error => {
          console.error('Error exporting MT798:', error);
          return EMPTY; // Return empty observable to handle the error
        })
      )
      .subscribe(result => {
        console.log('Deal exported successfully:', result);
      });
  }

}
