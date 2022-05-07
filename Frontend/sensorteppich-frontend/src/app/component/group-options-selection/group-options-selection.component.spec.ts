import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupOptionsSelectionComponent } from './group-options-selection.component';

describe('GroupOptionsSelectionComponent', () => {
  let component: GroupOptionsSelectionComponent;
  let fixture: ComponentFixture<GroupOptionsSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GroupOptionsSelectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupOptionsSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
