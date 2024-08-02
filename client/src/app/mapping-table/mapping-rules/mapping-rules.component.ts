import { Component, Input } from '@angular/core';
import { MtFieldMapping } from '../../graphql/types';

@Component({
  selector: 'app-mapping-rules',
  templateUrl: './mapping-rules.component.html',
  styleUrl: './mapping-rules.component.css'
})
export class MappingRulesComponent {

  @Input() mappingRule: any;
  @Input() tag: String = "";
  @Input() mt: String= "";

  isRulesDrawerOpen: boolean = false;
  mappingToUpdate: MtFieldMapping | null = null;
  
  constructor() { }

  ngOnInit(): void { }

  getKeys(obj: any): string[] {
    return Object.keys(obj);
  }

  clear(): void {
    this.mappingRule = null;
    this.tag = "";
    this.mt = "";
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
    this.isRulesDrawerOpen = isOpen;
    if (!isOpen) {
      // If drawer is closed, reset mapping to update
      this.mappingToUpdate = null;
    }
  }

}
