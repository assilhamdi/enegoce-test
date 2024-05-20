import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MappingTableManagementDrawerComponent } from './mapping-table-management-drawer.component';

describe('MappingTableManagementDrawerComponent', () => {
  let component: MappingTableManagementDrawerComponent;
  let fixture: ComponentFixture<MappingTableManagementDrawerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MappingTableManagementDrawerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MappingTableManagementDrawerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
