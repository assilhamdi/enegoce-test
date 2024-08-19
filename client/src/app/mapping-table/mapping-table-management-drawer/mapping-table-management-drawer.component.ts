import { Component, OnInit, Input, EventEmitter, Output, SimpleChanges } from '@angular/core';
import { MtFieldMappingInput, MtFieldMapping } from '../../graphql/types';
import { MappingService } from '../../services/mapping/mapping.service';

interface SelectedField {
  mrEntityName: string;
  mrDatabaseField: string;
}

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

  entities = ['DealComment', 'DealParty', 'InfoDeal', 'Settlement', 'Transport'];
  dbFields: string[] = [];
  mrFields: string[][] = [];
  mts = ['700', '701', '760', '761', '798'];


  newMapping: MtFieldMappingInput = {
    status: '',
    tag: '',
    fieldDescription: '',
    databaseField: '',
    entityName: '',
    fieldName: '',
    mt: '',
    fieldOrder: 0,
    fields: [],
    delimiter: '',
    code: ''
  };

  selectedFields: SelectedField[] = [{ mrEntityName: '', mrDatabaseField: '' }];

  mappingType: 'normal' | 'rules' = 'normal';

  currentMappingRule: any = {};

  constructor(private mappingService: MappingService) { }

  ngOnInit(): void {
    // Initialize form fields if mappingToUpdate is provided
    this.initializeForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['mappingToUpdate'] && !changes['mappingToUpdate'].firstChange) {
      this.initializeForm();
      this.updateCurrentMappingRule();
    }
  }

  onEntityChange(event: Event): void {
    const target = event.target as HTMLSelectElement | null;
    if (target) {
      this.newMapping.entityName = target.value;
      this.mappingService.getFieldByEntity(this.newMapping.entityName).subscribe(fields => {
        this.dbFields = fields;
      });
    }
  }

  onDynamicEntityChange(index: number, event: Event): void {
    const target = event.target as HTMLSelectElement | null;
    if (target) {
      const entityName = target.value;
      console.log(`Entity changed at index ${index}: ${entityName}`);
      this.selectedFields[index].mrEntityName = entityName;
      this.updateCurrentMappingRule();
      this.mappingService.getFieldByEntity(entityName).subscribe(fields => {
        console.log('Fields fetched for dynamic entity change:', entityName, fields);
        this.mrFields[index] = fields;
        this.updateCurrentMappingRule();
      });
    }
  }

  initializeForm(): void {
    if (this.mappingToUpdate) {

      console.log('mappingToUpdate:', this.mappingToUpdate);

      this.newMapping.status = this.mappingToUpdate.status ?? '';
      this.newMapping.tag = this.mappingToUpdate.tag ?? '';
      this.newMapping.fieldName = this.mappingToUpdate.fieldName ?? '';
      this.newMapping.fieldDescription = this.mappingToUpdate.fieldDescription ?? '';
      this.newMapping.entityName = this.mappingToUpdate.entityName ?? '';
      this.newMapping.mt = this.mappingToUpdate.mt ?? '';
      this.newMapping.fieldOrder = this.mappingToUpdate.fieldOrder ?? 0;

      this.mappingService.getFieldByEntity(this.newMapping.entityName).subscribe(fields => {
        console.log('Fields fetched for entity:', this.newMapping.entityName, fields);
        this.dbFields = fields;
        this.newMapping.databaseField = this.mappingToUpdate?.databaseField ?? '';
      });


      if (this.mappingToUpdate.mappingRule) {
        let mappingRuleString = this.mappingToUpdate.mappingRule.trim();

        // Remove leading and trailing quotes if they exist
        if ((mappingRuleString.startsWith('"') && mappingRuleString.endsWith('"')) ||
          (mappingRuleString.startsWith("'") && mappingRuleString.endsWith("'"))) {
          mappingRuleString = mappingRuleString.substring(1, mappingRuleString.length - 1);
        }

        try {
          const parsedRule = JSON.parse(mappingRuleString);
          console.log('Parsed mappingRule:', parsedRule);
          this.newMapping.delimiter = parsedRule.delimiter ?? '';
          this.newMapping.code = parsedRule.code ?? '';
          this.selectedFields = parsedRule.fields.map((field: string) => {
            const [entityName, databaseField] = field.split('.');
            return { mrEntityName: entityName, mrDatabaseField: databaseField };
          });

          // Fetch fields for each entity and populate this.fields accordingly
          this.mrFields = [];
          this.selectedFields.forEach((field, index) => {
            if (field.mrEntityName) {
              this.mappingService.getFieldByEntity(field.mrEntityName).subscribe(fields => {
                this.mrFields[index] = fields;
                console.log(index, fields);
              });
            } else {
              console.error('mrEntityName is null or undefined');
            }
          });
          this.mappingType = 'rules';
        } catch (error) {
          console.error('Error parsing mappingRule:', error);
        }
      } else {
        this.mappingType = 'normal';
        this.mrFields = [];
        this.selectedFields = [{ mrEntityName: '', mrDatabaseField: '' }];
        
      }
    } else {
      this.resetForm();
    }
  }

  onSubmit() {

    this.newMapping.fields = this.selectedFields.map(field => `${field.mrEntityName}.${field.mrDatabaseField}`);

    console.log('Submitting newMapping:', this.newMapping);
    console.log('current mapping rules fields', this.newMapping.fields);

    if (this.mappingToUpdate) {
      this.mappingService.updateFieldMapping(this.mappingToUpdate.id, this.newMapping).subscribe(
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
      this.mappingService.addMtFieldMapping(this.newMapping).subscribe(
        () => {
          console.log('Mapping added successfully');
          this.mappingAdded.emit();
          this.resetForm();
          this.closeDrawer();
        },
        error => {
          console.error('Error adding mapping:', error);
        }
      );
    }
  }

  addField() {
    this.selectedFields.push({ mrEntityName: '', mrDatabaseField: '' });
    this.mrFields.push([]);
  }

  removeField(index: number) {
    if (index > -1 && index < this.selectedFields.length) {
      this.selectedFields.splice(index, 1);
      this.mrFields.splice(index, 1);
      this.updateCurrentMappingRule();
    }
  }

  resetForm() {
    this.newMapping = {
      status: '',
      tag: '',
      fieldDescription: '',
      databaseField: '',
      entityName: '',
      fieldName: '',
      mt: '',
      fieldOrder: 0,
      fields: [],
      delimiter: '',
      code: ''
    };
    this.selectedFields = [{ mrEntityName: '', mrDatabaseField: '' }];
    this.mrFields = [[]];
    this.mappingToUpdate = null;
    this.mappingType = 'normal';
    this.currentMappingRule = {};
  }

  onMappingTypeChange(type: 'normal' | 'rules') {
    if (type === 'normal') {
      this.selectedFields.splice(0, this.selectedFields.length); // Remove all elements from the array
      this.newMapping.entityName = '';
      this.newMapping.databaseField = '';
    } else {
      if (this.selectedFields.length === 0) {
        this.addField(); // Add a new field to the array
      }
      this.newMapping.entityName = '';
      this.newMapping.databaseField = '';
      this.newMapping.delimiter = '';
      this.newMapping.code = '';
    }
  }

  updateCurrentMappingRule(): void {
    if (this.mappingType === 'rules') {
        this.currentMappingRule = {
            delimiter: this.newMapping.delimiter,
            code: this.newMapping.code,
            fields: this.selectedFields.map(field => `${field.mrEntityName}.${field.mrDatabaseField}`)
        };
    } else {
        this.currentMappingRule = {
            entityName: this.newMapping.entityName,
            databaseField: this.newMapping.databaseField
        };
    }
}

  openDrawer() {
    this.isOpen = true;
  }

  closeDrawer() {
    this.isOpen = false;
    this.resetForm();
    this.drawerStateChange.emit(this.isOpen);
  }

}
