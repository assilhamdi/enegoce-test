import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { MappingService } from '../../../services/mapping/mapping.service';
import { MtFieldMapping, MtFieldMappingInput } from '../../../graphql/types';

interface SelectedField {
  entityName: string;
  databaseField: string;
}

@Component({
  selector: 'app-mapping-rules-management-drawer',
  templateUrl: './mapping-rules-management-drawer.component.html',
  styleUrl: './mapping-rules-management-drawer.component.css'
})
export class MappingRulesManagementDrawerComponent implements OnInit, OnChanges {

  @Input() openForAdd: boolean = false;
  @Input() openForUp: boolean = false;
  @Output() drawerStateChange = new EventEmitter<boolean>();
  @Input() mappingToUpdate: MtFieldMapping | null = null;

  entities = ['DealComment', 'DealParty', 'InfoDeal', 'Settlement', 'Transport'];
  fields: string[][] = [];
  selectedFields: SelectedField[] = [{ entityName: '', databaseField: '' }];

  newMapping: MtFieldMappingInput = {
    status: '',
    tag: '',
    fieldDescription: '',
    databaseField: '',
    entityName: '',
    mt: '',
    fieldOrder: 0,
    fields: [],
    delimiter: '',
    code: ''
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
    if (this.openForUp) {
      this.openForUp = false;
      this.drawerStateChange.emit(this.openForUp);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['mappingToUpdate'] && !changes['mappingToUpdate'].firstChange) {
      this.initializeForm();
    }
  }

  onEntityChange(index: number, event: Event): void {
    const target = event.target as HTMLSelectElement | null;
    if (target) {
      const entityName = target.value;
      this.selectedFields[index].entityName = entityName;
      this.mappingService.getFieldByEntity(entityName).subscribe(fields => {
        this.fields[index] = fields;
      });
    }
  }

  initializeForm(): void {
    if (this.mappingToUpdate) {
      console.log('Initializing form with:', this.mappingToUpdate);
      this.newMapping.status = this.mappingToUpdate.status ?? '';
      this.newMapping.tag = this.mappingToUpdate.tag ?? '';
      this.newMapping.fieldDescription = this.mappingToUpdate.fieldDescription ?? '';
      this.newMapping.entityName = this.mappingToUpdate.entityName ?? '';
      this.newMapping.mt = this.mappingToUpdate.mt ?? '';
      this.newMapping.fieldOrder = this.mappingToUpdate.fieldOrder ?? 0;
  
      if (this.mappingToUpdate.mappingRule) {
        let mappingRuleString = this.mappingToUpdate.mappingRule.trim();
        
        // Remove leading and trailing quotes if they exist
        if ((mappingRuleString.startsWith('"') && mappingRuleString.endsWith('"')) ||
            (mappingRuleString.startsWith("'") && mappingRuleString.endsWith("'"))) {
          mappingRuleString = mappingRuleString.substring(1, mappingRuleString.length - 1);
        }
  
        try {
          const parsedRule = JSON.parse(mappingRuleString);
          this.newMapping.delimiter = parsedRule.delimiter ?? '';
          this.newMapping.code = parsedRule.code ?? '';
          this.selectedFields = parsedRule.fields.map((field: string) => {
            const [entityName, databaseField] = field.split('.');
            return { entityName, databaseField };
          });
  
          // Fetch fields for each entity and populate this.fields accordingly
          this.fields = [];
          this.selectedFields.forEach((field, index) => {
            this.mappingService.getFieldByEntity(field.entityName).subscribe(fields => {
              this.fields[index] = fields;
              const matchingField = fields.find(f => f === field.databaseField);
              if (matchingField) {
                this.selectedFields[index].databaseField = matchingField;
              }
            });
          });
        } catch (error) {
          console.error('Error parsing mappingRule:', error);
        }
      }
    } else {
      this.resetForm();
    }
  }
  

  parseMappingRule(mappingRule: string): SelectedField[] {
    // Trim whitespace and remove surrounding quotes
    let sanitizedMappingRule = mappingRule.trim();
    if ((sanitizedMappingRule.startsWith('"') && sanitizedMappingRule.endsWith('"')) ||
        (sanitizedMappingRule.startsWith("'") && sanitizedMappingRule.endsWith("'"))) {
      sanitizedMappingRule = sanitizedMappingRule.substring(1, sanitizedMappingRule.length - 1);
      console.log("newruole:",sanitizedMappingRule );
    }
  
    try {
      const parsedRule = JSON.parse(sanitizedMappingRule);
      return parsedRule.fields.map((field: string) => {
        const [entityName, databaseField] = field.split('.');
        return { entityName, databaseField };
      });
    } catch (error) {
      console.error('Error parsing mappingRule:', error);
      return [];
    }
  }

  onSubmit() {
    this.newMapping.fields = this.selectedFields.map(field => `${field.entityName}.${field.databaseField}`);

    if (this.mappingToUpdate) {
      this.mappingService.updateMtFieldMappingRule(this.mappingToUpdate.id, this.newMapping.fields, this.newMapping.delimiter, this.newMapping.code).subscribe(
        () => {
          console.log('Mapping rule updated successfully');
          this.resetForm();
          this.closeDrawer();
        },
        error => {
          console.error('Error updating mapping rule:', error);
        }
      );
    } else {
      this.mappingService.addMtFieldMapping(this.newMapping).subscribe(
        () => {
          console.log('Mapping rule added successfully');
          this.resetForm();
          this.closeDrawer();
        },
        error => {
          console.error('Error adding mapping rule:', error);
        }
      );
    }
  }

  addField() {
    this.selectedFields.push({ entityName: '', databaseField: '' });
    this.fields.push([]);
  }

  removeField(index: number) {
    this.selectedFields.splice(index, 1);
    this.fields.splice(index, 1);
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
      fields: [],
      delimiter: '',
      code: ''
    };
    this.selectedFields = [{ entityName: '', databaseField: '' }];
    this.fields = [[]];
  }
}
