import { TestBed } from '@angular/core/testing';

import { CaslService } from './casl.service';

describe('CaslService', () => {
  let service: CaslService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CaslService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
