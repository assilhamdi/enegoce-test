import { TestBed } from '@angular/core/testing';

import { MTService } from './mt.service';

describe('MTService', () => {
  let service: MTService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MTService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
