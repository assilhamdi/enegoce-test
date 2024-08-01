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
  selectedMt: string = "";
  selectedSt: string = "";
  dfFilter: String = "";
  order: boolean = true;
  isOpen: boolean = false;
  mappingToUpdate: MtFieldMapping | null = null; // Holds the mapping to update
  mappingRule: any;
  tag: String = "";
  mt: String = "";

  @Output() mappingRuleFetched = new EventEmitter<any>();
  @Output() tagFetched = new EventEmitter<String>();
  @Output() mtFetched = new EventEmitter<String>();


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

  filterMappingsByMt(): void {
    if (this.selectedMt !== 'All') {
      this.mappingService.MappingsByMT(this.selectedMt).subscribe(
        mappings => {
          this.mappings = mappings;
        },
        error => {
          console.error('Error fetching mappings by MT:', error);
        }
      );
    } else {
      this.fetchMappings();
    }
  }

  filterMappingsBySt(): void {
    this.mappingService.MappingsByST(this.selectedSt).subscribe(
      mappings => {
        this.mappings = mappings;
      },
      error => {
        console.error('Error fetching mappings by Status:', error);
      }
    );
  }

  filterMappingsByDf(): void {
    this.mappingService.MappingsByFD(this.dfFilter).subscribe(
      mappings => {
        this.mappings = mappings;
      },
      error => {
        console.error('Error fetching mappings by Database Field:', error);
      }
    );
  }

  resetFilter(): void {
    // Reset the selected MT and show all mappings
    this.selectedMt = 'All';
    this.fetchMappings();
  }
  
  fetchMappingRule(id: Number) {
    this.mappingService.getMappingRule(id).subscribe(
      (rule) => {
        this.mappingRule = rule; // Set the fetched rule
        this.mappingRuleFetched.emit(rule); // Emit the fetched rule
        console.log(rule);
      },
      (error) => {
        console.error('Error fetching mapping rule:', error);
      }
    );
  }

  fetchDetails(id: Number) {
    this.mappingService.getMappingById(id).subscribe(
      (mapping) => {
        this.tag = mapping.tag;
        this.mt = mapping.mt;
        this.tagFetched.emit(this.tag);
        this.mtFetched.emit(this.mt);
      },
      (error) => {
        console.error('Error fetching mapping:', error);
      }
    )
  }

  display(id: Number) {
    this.fetchMappingRule(id);
    this.fetchDetails(id);
  }

}
