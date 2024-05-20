import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { MtFieldMapping } from '../graphql/types';
import { MappingService } from '../services/mapping/mapping.service';

@Component({
  selector: 'app-mapping-table',
  templateUrl: './mapping-table.component.html',
  styleUrl: './mapping-table.component.css'
})
export class MappingTableComponent implements OnInit {

  mappings: MtFieldMapping[] = [];
  order: boolean = true;
  isOpen: boolean = false;
  mappingToUpdate: MtFieldMapping | null = null; // Holds the mapping to update


  constructor(private mappingService: MappingService) { }

  ngOnInit(): void {
    this.fetchMappings();
  }

  fetchMappings() {
    this.mappingService.getAllMappings().subscribe(mappings => {
      this.mappings = mappings
    });
  }

  sortByOrder(order: boolean) {
    this.mappingService.orderMappingsByFO(order).subscribe({
      next: mappings => {
        this.mappings = mappings;
      },
      error: error => {
        console.error('Error fetching mappings:', error);
      }
    });
  }

  openDrawer(mappingToUpdate: MtFieldMapping | null = null): void {
    this.mappingToUpdate = mappingToUpdate; // Set the mapping to update if provided
    this.isOpen = true;
  }
  

  closeDrawer(): void {
    this.isOpen = false;
    this.mappingToUpdate = null; // Reset mapping to update
  }

  handleDrawerStateChange(isOpen: boolean): void {
    this.isOpen = isOpen;
    if (!isOpen) {
      // If drawer is closed, reset mapping to update
      this.mappingToUpdate = null;
    }
  }

}
