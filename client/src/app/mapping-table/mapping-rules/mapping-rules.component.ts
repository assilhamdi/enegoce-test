import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MtFieldMapping } from '../../graphql/types';
import { MappingService } from '../../services/mapping/mapping.service';

@Component({
  selector: 'app-mapping-rules',
  templateUrl: './mapping-rules.component.html',
  styleUrl: './mapping-rules.component.css'
})
export class MappingRulesComponent implements OnInit {

  @Input() mappingRule: any;
  @Input() mapping: any;
  @Output() mappingUpdated = new EventEmitter<MtFieldMapping | null>();

  isRulesDrawerOpen: boolean = false;
  mappingToUpdate: MtFieldMapping | null = null;
  
  constructor(private mappingService: MappingService) { }

  ngOnInit(): void {

   }

  getKeys(obj: any): string[] {
    return Object.keys(obj);
  }

  clear(): void {
    this.mappingRule = null;
    this.mappingToUpdate = null;
    this.mapping=null;
    console.log("current mapping : ",this.mapping);
  }

  openRulesDrawer(mappingToUpdate: MtFieldMapping | null = null): void {
    this.mappingToUpdate = mappingToUpdate; // Set the mapping to update if provided
    this.isRulesDrawerOpen = true;
    this.mappingUpdated.emit(this.mappingToUpdate);
  }

  closeRulesDrawer(): void {
    this.isRulesDrawerOpen = false;
    this.mappingToUpdate = null; // Reset mapping to update
  }

  handleDrawerStateChange(isOpen: boolean): void {
    this.isRulesDrawerOpen = isOpen;
    if (!isOpen) {
      // If drawer is closed, reset mapping to update
      this.mappingToUpdate = null;
    }
  }

  delimiterFormatter(value: string): string {
    if (value === '') {
      return '[empty]'; // Placeholder for empty string
    } else if (typeof value === 'string') {
      let formattedValue = value.replace(/\n/g, '[new-line]'); //Replacing spaces
      formattedValue = formattedValue.replace(/ /g, '[space]'); //Replacing new lines
      return formattedValue;
    }
    return value;
  }

}
