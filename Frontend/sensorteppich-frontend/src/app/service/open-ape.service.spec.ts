import { TestBed } from '@angular/core/testing';

import { OpenAPEService } from './open-ape.service';

describe('OpenAPEServiceService', () => {
  let service: OpenAPEService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OpenAPEService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
