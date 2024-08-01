import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-mapping-rules',
  templateUrl: './mapping-rules.component.html',
  styleUrl: './mapping-rules.component.css'
})
export class MappingRulesComponent {

  @Input() mappingRule: any;
  @Input() tag: String = "";
  @Input() mt: String= "";
  
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

}
