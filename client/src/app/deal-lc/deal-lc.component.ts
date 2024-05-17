import { Component, OnInit } from '@angular/core';
import { DealLC } from '../graphql/types';
import { DealLcService } from '../services/deal-lc/deal-lc.service';
import { catchError } from 'rxjs/operators';
import { EMPTY } from 'rxjs';

@Component({
  selector: 'app-deal-lc',
  templateUrl: './deal-lc.component.html',
  styleUrl: './deal-lc.component.css'
})
export class DealLcComponent implements OnInit {

  deals: DealLC[] = [];
  isOpen: boolean = false;
  operationSuccess: boolean | null = null;

  constructor(private dealService: DealLcService) { }

  ngOnInit(): void {
    this.fetchDeals();
  }

  fetchDeals() {
    this.dealService.getAllDealLCs().subscribe(deals => { this.deals = deals; });
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

  exportMT700(dealId: Number) {
    this.dealService.exportMT700(dealId)
      .pipe(
        catchError(error => {
          console.error('Error exporting MT700:', error);
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
