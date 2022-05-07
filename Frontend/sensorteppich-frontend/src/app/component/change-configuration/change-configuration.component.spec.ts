import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeConfigurationComponent } from './change-configuration.component';

describe('ChangeConfigurationComponent', () => {
  let component: ChangeConfigurationComponent;
  let fixture: ComponentFixture<ChangeConfigurationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChangeConfigurationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangeConfigurationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
