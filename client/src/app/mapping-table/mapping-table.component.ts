import { Component, OnInit, Input, EventEmitter, Output, SimpleChanges } from '@angular/core';
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
  order: boolean = true;
  isMappingDrawerOpen: boolean = false;
  isRulesDrawerOpen: boolean = false;
  mappingToUpdate: MtFieldMapping | null = null; // Holds the mapping to update
  mappingRule: any;
  tag: String = "";
  mt: String = "";

  private filterSubject: Subject<string> = new Subject<string>();

  @Output() mappingRuleFetched = new EventEmitter<any>();
  @Output() tagFetched = new EventEmitter<String>();
  @Output() mtFetched = new EventEmitter<String>();
  @Output() mappingFetched = new EventEmitter<any>();


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

  openRulesDrawer(mappingToUpdate: MtFieldMapping | null = null): void {
    this.mappingToUpdate = mappingToUpdate; // Set the mapping to update if provided
    this.isRulesDrawerOpen = true;
  }


  closeRulesDrawer(): void {
    this.isRulesDrawerOpen = false;
    this.mappingToUpdate = null; // Reset mapping to update
  }

  handleDrawerStateChange(isOpen: boolean): void {
    this.isMappingDrawerOpen = isOpen;
    this.isRulesDrawerOpen = isOpen;
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
        this.mappingToUpdate = mapping;
        this.mappingFetched.emit(mapping);
        console.log('displaying fetched mapping :',mapping);
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
