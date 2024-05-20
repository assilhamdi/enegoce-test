import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { MtFieldMappingInput } from '../../graphql/types';
import { MappingService } from '../../services/mapping/mapping.service';

@Component({
  selector: 'app-mapping-table-management-drawer',
  templateUrl: './mapping-table-management-drawer.component.html',
  styleUrl: './mapping-table-management-drawer.component.css'
})

export class MappingTableManagementDrawerComponent implements OnInit {

  @Input() isOpen: boolean = false;
  @Output() drawerStateChange = new EventEmitter<boolean>();

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

  ngOnInit(): void { }

  openDrawer() {
    this.isOpen = true;
  }

  closeDrawer() {
    this.isOpen = false;
    this.drawerStateChange.emit(this.isOpen);
  }

  onSubmit() {
    this.mappingService.addMtFieldMapping(this.newMapping).subscribe(
      result => {
        console.log('Mapping added successfully:', result);
        // Reset form and close drawer
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
        this.closeDrawer();
      },
      error => {
        console.error('Error adding mapping:', error);
      }
    );
  }

}
