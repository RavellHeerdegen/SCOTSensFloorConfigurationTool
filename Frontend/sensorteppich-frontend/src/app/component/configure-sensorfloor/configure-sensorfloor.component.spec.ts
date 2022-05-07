import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigureSensorfloorComponent } from './configure-sensorfloor.component';

describe('ConfigureSensorfloorComponent', () => {
  let component: ConfigureSensorfloorComponent;
  let fixture: ComponentFixture<ConfigureSensorfloorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfigureSensorfloorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigureSensorfloorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
