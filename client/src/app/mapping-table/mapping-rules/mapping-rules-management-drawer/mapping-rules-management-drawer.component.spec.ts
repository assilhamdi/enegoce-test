import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MappingRulesManagementDrawerComponent } from './mapping-rules-management-drawer.component';

describe('MappingRulesManagementDrawerComponent', () => {
  let component: MappingRulesManagementDrawerComponent;
  let fixture: ComponentFixture<MappingRulesManagementDrawerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MappingRulesManagementDrawerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MappingRulesManagementDrawerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
