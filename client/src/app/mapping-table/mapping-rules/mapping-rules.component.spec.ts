import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MappingRulesComponent } from './mapping-rules.component';

describe('MappingRulesComponent', () => {
  let component: MappingRulesComponent;
  let fixture: ComponentFixture<MappingRulesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MappingRulesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MappingRulesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
