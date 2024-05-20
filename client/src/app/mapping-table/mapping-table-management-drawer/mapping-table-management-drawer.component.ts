import { Component, OnInit, Input, EventEmitter, Output, SimpleChanges } from '@angular/core';
import { MtFieldMappingInput, MtFieldMapping } from '../../graphql/types';
import { MappingService } from '../../services/mapping/mapping.service';

@Component({
  selector: 'app-mapping-table-management-drawer',
  templateUrl: './mapping-table-management-drawer.component.html',
  styleUrl: './mapping-table-management-drawer.component.css'
})

export class MappingTableManagementDrawerComponent implements OnInit {

  @Input() isOpen: boolean = false;
  @Input() mappingToUpdate: MtFieldMapping | null = null; // Input for mapping to update
  @Output() drawerStateChange = new EventEmitter<boolean>();  
  @Output() mappingAdded = new EventEmitter<void>();
  @Output() mappingUpdated = new EventEmitter<void>();

  newMapping: MtFieldMappingInput = {
    status: '',
    tag: '',
    fieldDescription: '',
    mappingRule: '',
    databaseField: '',
    entityName: '',
    mt: '',
    fieldOrder: 0,
  };

  constructor(private mappingService: MappingService) { }

  ngOnInit(): void {
    // Initialize form fields if mappingToUpdate is provided
    this.initializeForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    // React to changes in mappingToUpdate
    if (changes['mappingToUpdate'] && !changes['mappingToUpdate'].firstChange) {
      // Re-initialize form fields if mappingToUpdate changes
      this.initializeForm();
    }
  }

  initializeForm(): void {
    if (this.mappingToUpdate) {
      // Populate form fields with mappingToUpdate data
      this.newMapping.status = this.mappingToUpdate.status;
      this.newMapping.tag = this.mappingToUpdate.tag;
      this.newMapping.fieldDescription = this.mappingToUpdate.fieldDescription;
      this.newMapping.databaseField = this.mappingToUpdate.databaseField;
      this.newMapping.entityName = this.mappingToUpdate.entityName;
      this.newMapping.mt = this.mappingToUpdate.mt;
      this.newMapping.fieldOrder = this.mappingToUpdate.fieldOrder;
    } else {
      // Reset form fields if mappingToUpdate is null
      this.resetForm();
    }
  }

  openDrawer() {
    this.isOpen = true;
  }

  closeDrawer() {
    this.isOpen = false;
    this.drawerStateChange.emit(this.isOpen);
  }

  onSubmit() {
    if (this.mappingToUpdate) {
      // If mappingToUpdate exists, it means we're updating
      this.mappingService.updateMtFieldMapping(this.mappingToUpdate.id, this.newMapping).subscribe(
        () => {
          console.log('Mapping updated successfully');
          this.mappingUpdated.emit();
          this.resetForm();
          this.closeDrawer();
        },
        error => {
          console.error('Error updating mapping:', error);
        }
      );
    } else {
      // Otherwise, we're adding a new mapping
      this.mappingService.addMtFieldMapping(this.newMapping).subscribe(
        () => {
          console.log('Mapping added successfully');          
          this.mappingAdded.emit();
          // Reset form fields and close drawer
          this.resetForm();
          this.closeDrawer();
        },
        error => {
          console.error('Error adding mapping:', error);
        }
      );
    }
  }

  resetForm() {
    this.newMapping = {
      status: '',
      tag: '',
      fieldDescription: '',
      mappingRule: '',
      databaseField: '',
      entityName: '',
      mt: '',
      fieldOrder: 0,
    };
  }

}
