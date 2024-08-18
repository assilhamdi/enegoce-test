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
  
  constructor() { }

  ngOnInit(): void {

   }

  getKeys(obj: any): string[] {
    return Object.keys(obj);
  }

  clear(): void {
    this.mappingRule = null;
    this.mapping=null;
    console.log("current mapping : ",this.mapping);
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
