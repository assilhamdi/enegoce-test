import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DealLcComponent } from './deal-lc.component';

describe('DealLcComponent', () => {
  let component: DealLcComponent;
  let fixture: ComponentFixture<DealLcComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DealLcComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DealLcComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
