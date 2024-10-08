import { Component, OnInit, EventEmitter, Output, SimpleChanges } from '@angular/core';
import { MtFieldMapping } from '../graphql/types';
import { MappingService } from '../services/mapping/mapping.service';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

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
  filter: String = "";
  isMappingDrawerOpen: boolean = false;
  mappingToUpdate: MtFieldMapping | null = null; // Holds the mapping to update

  private filterSubject: Subject<string> = new Subject<string>();


  constructor(private mappingService: MappingService) { }

  ngOnInit(): void {
    this.fetchMappings();
    this.fetchUniqueMts();

    this.filterSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(filterValue => {
      this.filterMappings();
    });
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
      },
      error => {
        console.error('Error fetching unique MTs:', error);
      }
    );
  }

  openMappingDrawer(mappingToUpdate: MtFieldMapping | null = null): void {
    this.mappingToUpdate = mappingToUpdate; // Set the mapping to update if provided
    this.isMappingDrawerOpen = true;
  }


  closeMappingDrawer(): void {
    this.isMappingDrawerOpen = false;
    this.mappingToUpdate = null; // Reset mapping to update
  }

  handleDrawerStateChange(isOpen: boolean): void {
    this.isMappingDrawerOpen = isOpen;
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

  filterMappings(): void {
    this.mappingService.findByFilter(this.filter).subscribe(
      mappings => {
        this.mappings = mappings;
      },
      error => {
        console.error('Error fetching mappings by filter:', error);
      }
    );
  }

  onFilterChange(value: string): void {
    this.filterSubject.next(value);
  }

  resetFilter(): void {
    // Reset the selected MT and show all mappings
    this.selectedMt = 'All';
    this.selectedSt = '';
    this.filter ='';
    this.fetchMappings();
  }

}
