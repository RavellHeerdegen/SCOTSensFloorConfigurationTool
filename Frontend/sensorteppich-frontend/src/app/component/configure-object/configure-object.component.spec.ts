import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigureObjectComponent } from './configure-object.component';

describe('ConfigureObjectComponent', () => {
  let component: ConfigureObjectComponent;
  let fixture: ComponentFixture<ConfigureObjectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfigureObjectComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigureObjectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
