import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OverwriteConfigurationComponent } from './overwrite-configuration.component';

describe('OverwriteConfigurationComponent', () => {
  let component: OverwriteConfigurationComponent;
  let fixture: ComponentFixture<OverwriteConfigurationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OverwriteConfigurationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OverwriteConfigurationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
