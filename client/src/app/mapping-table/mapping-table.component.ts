import { Component, OnInit, Input, EventEmitter, Output, SimpleChanges } from '@angular/core';
import { MtFieldMapping } from '../graphql/types';
import { MappingService } from '../services/mapping/mapping.service';

@Component({
  selector: 'app-mapping-table',
  templateUrl: './mapping-table.component.html',
  styleUrl: './mapping-table.component.css'
})
export class MappingTableComponent implements OnInit {

  mappings: MtFieldMapping[] = [];
  distinctMtValues: string[] = [];
  selectedMt: string | null = null;
  order: boolean = true;
  isOpen: boolean = false;
  mappingToUpdate: MtFieldMapping | null = null; // Holds the mapping to update


  constructor(private mappingService: MappingService) { }

  ngOnInit(): void {
    this.fetchMappings();
    this.fetchUniqueMts();
  }

  ngOnChanges(changes: SimpleChanges): void {
    // React to changes in mappingToUpdate
    if (changes['mappingToUpdate'] && !changes['mappingToUpdate'].firstChange) {
      // If mappingToUpdate changes, refresh mappings
      this.fetchMappings();
    }
  }

  fetchMappings(): void {
    this.mappingService.getAllMappings().subscribe(
      mappings => {
        this.mappings = mappings;
      },
      error => {
        console.error('Error fetching mappings:', error);
      }
    );
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

  refreshMappings(): void {
    this.fetchMappings();
  }


  deleteMapping(id: Number): void {
    this.mappingService.deleteFieldMapping(id).subscribe(
      success => {
        if (success) {
          console.log('Mapping deleted successfully');
          this.refreshMappings();
        } else {
          console.error('Failed to delete mapping');
        }
      },
      error => {
        console.error('Error deleting mapping:', error);
      }
    );
  }


  ////////////////// FILTERING ////////////////
  /////////////////////////////////////////////



  /*filterMappingsByMt(): void {
    if (this.selectedMt) {
      this.mappingService.MappingsByMT(this.selectedMt).subscribe(
        mappings => {
          this.mappings = mappings;
        },
        error => {
          console.error('Error fetching filtered mappings:', error);
        }
      );
    } else {
      this.fetchMappings();
    }
  }*/

  filterMappingsByMt(): void {
    if (this.selectedMt !== 'All') {
      // Filter the mappings based on the selected MT
      this.mappings = this.mappings.filter(mapping => mapping.mt === this.selectedMt);
    } else {
      // If 'All' is selected, show all mappings
      this.fetchMappings();
    }
  }

  resetFilter(): void {
    // Reset the selected MT and show all mappings
    this.selectedMt = 'All';
    this.fetchMappings();
  }

}
