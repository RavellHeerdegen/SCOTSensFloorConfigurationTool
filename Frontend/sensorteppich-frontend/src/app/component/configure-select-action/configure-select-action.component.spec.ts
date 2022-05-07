import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigureSelectActionComponent } from './configure-select-action.component';

describe('ConfigureSelectActionComponent', () => {
  let component: ConfigureSelectActionComponent;
  let fixture: ComponentFixture<ConfigureSelectActionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfigureSelectActionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigureSelectActionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
