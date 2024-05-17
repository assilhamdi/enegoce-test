import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DealLcAddDrawerComponent } from './deal-lc-add-drawer.component';

describe('DealLcAddDrawerComponent', () => {
  let component: DealLcAddDrawerComponent;
  let fixture: ComponentFixture<DealLcAddDrawerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DealLcAddDrawerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DealLcAddDrawerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
