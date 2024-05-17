import { Component, OnInit } from '@angular/core';
import { MtFieldMapping } from '../graphql/types';
import { MappingService } from '../services/mapping/mapping.service';

@Component({
  selector: 'app-mapping-table',
  templateUrl: './mapping-table.component.html',
  styleUrl: './mapping-table.component.css'
})
export class MappingTableComponent implements OnInit{

  mappings: MtFieldMapping [] = [];

  constructor(private mappingService: MappingService) {}

  ngOnInit(): void {
    this.fetchMappings();
  }

  fetchMappings() {
    this.mappingService.getAllMappings().subscribe(mappings => {
      this.mappings = mappings
    });
  }

}
