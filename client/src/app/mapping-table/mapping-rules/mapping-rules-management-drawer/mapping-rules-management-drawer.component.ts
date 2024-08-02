import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { MappingService } from '../../../services/mapping/mapping.service';
import { MtFieldMapping, MtFieldMappingInput } from '../../../graphql/types';

@Component({
  selector: 'app-mapping-rules-management-drawer',
  templateUrl: './mapping-rules-management-drawer.component.html',
  styleUrl: './mapping-rules-management-drawer.component.css'
})
export class MappingRulesManagementDrawerComponent {

  @Input() openForAdd: boolean = false;
  @Input() openForUp: boolean = false;
  @Output() drawerStateChange = new EventEmitter<boolean>();  
  @Input() mappingToUpdate: MtFieldMapping | null = null;

  newMapping: MtFieldMappingInput = {
    status: '',
    tag: '',
    fieldDescription: '',
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

  openDrawerForAdd() {
    this.openForAdd = true;
  }

  openDrawerForUp() {
    this.openForUp = true;
  }

  closeDrawer() {
    if (this.openForAdd) {
      this.openForAdd = false;
      this.drawerStateChange.emit(this.openForAdd);
    }
    if (this.openForUp) {this.openForUp = false;}
    this.drawerStateChange.emit(this.openForUp);
  }

  ngOnChanges(changes: SimpleChanges): void {
    // React to changes in mappingToUpdate
    if (changes['mappingToUpdate'] && !changes['mappingToUpdate'].firstChange) {
      // Re-initialize form fields if mappingToUpdate changes
      //this.initializeForm();
    }
  }

  initializeForm(): void {
    if (this.mappingToUpdate) {
      // Populate form fields with mappingToUpdate data
      this.newMapping.status = this.mappingToUpdate.status ?? '';
      this.newMapping.tag = this.mappingToUpdate.tag ?? '';
      console.log(this.mappingToUpdate.tag);
      this.newMapping.fieldDescription = this.mappingToUpdate.fieldDescription ?? '';
      this.newMapping.entityName = this.mappingToUpdate.entityName ?? '';
      this.newMapping.mt = this.mappingToUpdate.mt ?? '';
      console.log(this.mappingToUpdate.mt);
      this.newMapping.fieldOrder = this.mappingToUpdate.fieldOrder ?? 0;
    } else {
      // Reset form fields if mappingToUpdate is null
      this.resetForm();
    }
  }

  resetForm() {
    this.newMapping = {
      status: '',
      tag: '',
      fieldDescription: '',
      databaseField: '',
      entityName: '',
      mt: '',
      fieldOrder: 0,
    };
  }
}
