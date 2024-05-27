import { Component, OnInit } from '@angular/core';
import { DealLC } from '../graphql/types';
import { DealLcService } from '../services/deal-lc/deal-lc.service';
import { catchError } from 'rxjs/operators';
import { EMPTY } from 'rxjs';
import { MappingService } from '../services/mapping/mapping.service';

@Component({
  selector: 'app-deal-lc',
  templateUrl: './deal-lc.component.html',
  styleUrl: './deal-lc.component.css'
})
export class DealLcComponent implements OnInit {

  deals: DealLC[] = [];
  isOpen: boolean = false;
  operationSuccess: boolean | null = null;
  distinctMtValues: string[] = [];
  selectedMt: string = "";
  selectedSubMt: string = "";

  constructor(private dealService: DealLcService, private mappingService: MappingService) { }

  ngOnInit(): void {
    this.fetchDeals();
    this.fetchUniqueMts();
  }

  fetchDeals() {
    this.dealService.getAllDealLCs().subscribe(deals => { this.deals = deals; });
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

  exportDeal(dealId: Number) {
    this.dealService.exportDeal(dealId)
      .pipe(
        catchError(error => {
          console.error('Error exporting deal:', error);
          return EMPTY; // Return empty observable to handle the error
        })
      )
      .subscribe(result => {
        console.log('Deal exported successfully:', result);
      });
  }

  exportMT(dealId: Number, mt: String) {
    this.dealService.exportMT(dealId, mt)
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

  exportMT798(id: Number, mt: String) {
    this.dealService.exportMT798(id, mt)
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

  openDrawer(): void {
    this.isOpen = true;
  }

  closeDrawer(): void {
    this.isOpen = false;
  }

  handleDrawerStateChange(isOpen: boolean): void {
    this.isOpen = isOpen;
  }

  handleOperationSuccess(success: boolean) {
    this.operationSuccess = success;
    setTimeout(() => {
      this.dismissFeedback(); // Dismiss feedback after a certain time
    }, 5000); // Dismiss after 5 seconds (adjust as needed)
  }

  handleDealAdded() {
    this.fetchDeals(); // Fetch deals again when a deal is added
  }

  dismissFeedback() {
    this.operationSuccess = null;
  }

}
