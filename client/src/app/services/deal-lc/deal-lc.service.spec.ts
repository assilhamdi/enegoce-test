import { TestBed } from '@angular/core/testing';

import { DealLcService } from './deal-lc.service';

describe('DealLcService', () => {
  let service: DealLcService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DealLcService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
