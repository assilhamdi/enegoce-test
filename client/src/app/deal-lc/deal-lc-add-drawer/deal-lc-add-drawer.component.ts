import { Component, OnInit, Input, EventEmitter, Output} from '@angular/core';
import { DealLCInput } from '../../graphql/types';

import { CREATE_DEAL_LC } from '../../graphql/graphql.queries_deal-lc';
import { DealLcService } from '../../services/deal-lc/deal-lc.service';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';

@Component({
  selector: 'app-deal-lc-add-drawer',
  templateUrl: './deal-lc-add-drawer.component.html',
  styleUrl: './deal-lc-add-drawer.component.css'
})
export class DealLcAddDrawerComponent implements OnInit {

  
  @Input() isOpen: boolean = false;
  @Output() drawerStateChange = new EventEmitter<boolean>();
  @Input() operationSuccess: boolean | null = null;
  @Output() operationSuccessChange: EventEmitter<boolean | null> = new EventEmitter<boolean | null>();
  @Output() dealAdded = new EventEmitter<void>();

  
  newDeal: DealLCInput = {
    formLC: '',
    dueDate: '',
    expiryDate: '',
    expiryPlace: '',
    customerReference: '',
    counterParty: '',
    bankISSRef: '',
    bankRMBRef: '',
    creationDate: '',
    currencyId: '',
    lcAmount: '',
    varAmountTolerance: '',
    availableWith: '',
    partialTranshipment: '',
    transhipment: '',
  };


  constructor(private dealService: DealLcService) {}

  ngOnInit(): void {}

  openDrawer() {
    this.isOpen = true;
  }

  closeDrawer() {
    this.isOpen = false;
    this.drawerStateChange.emit(this.isOpen);
  }

  sucess() {
    this.operationSuccess = true;
    this.operationSuccessChange.emit(this.operationSuccess);
  }

  onDateSelected(event: MatDatepickerInputEvent<Date>): void {
    this.newDeal.dueDate = event.value?.toISOString() || '';
    this.newDeal.expiryDate = event.value?.toISOString() || '';
  }

  onSubmit() {
    this.dealService.addDealLC(this.newDeal).subscribe(
      result => {
        console.log('Deal created successfully:', result);
        // Reset form and close drawer
        this.newDeal = {
          formLC: '',
          dueDate: '',
          expiryDate: '',
          expiryPlace: '',
          customerReference: '',
          counterParty: '',
          bankISSRef: '',
          bankRMBRef: '',
          creationDate: '',
          currencyId: '',
          lcAmount: '',
          varAmountTolerance: '',
          availableWith: '',
          partialTranshipment: '',
          transhipment: ''
        };
        this.closeDrawer();
        this.dealAdded.emit();
        this.sucess();
      },
      error => {
        console.error('Error creating deal:', error);
      }
    );
  }

}
