import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LibraryOtherComponent } from './library-other.component';

describe('LibraryOtherComponent', () => {
  let component: LibraryOtherComponent;
  let fixture: ComponentFixture<LibraryOtherComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LibraryOtherComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LibraryOtherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
