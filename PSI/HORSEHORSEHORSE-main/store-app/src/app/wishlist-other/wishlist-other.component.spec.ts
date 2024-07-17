import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WishlistOtherComponent } from './wishlist-other.component';

describe('WishlistOtherComponent', () => {
  let component: WishlistOtherComponent;
  let fixture: ComponentFixture<WishlistOtherComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WishlistOtherComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WishlistOtherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
