import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { StoreComponent } from './store/store.component';
import { AppRoutingModule } from './app-routing.module';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { CheckoutComponent } from './checkout/checkout.component';
import { LibraryComponent } from './library/library.component';
import { ProductDetailComponent } from './product-detail/product-detail.component';
import { WishlistComponent } from './wishlist/wishlist.component';
import { UserLoginComponent } from './user-login/user-login.component';
import { FollowingComponent } from './following/following.component';
import { WishlistOtherComponent } from './wishlist-other/wishlist-other.component';
import { LibraryOtherComponent } from './library-other/library-other.component';

@NgModule({
  declarations: [
    AppComponent,
    StoreComponent,
    UserProfileComponent,
    CheckoutComponent,
    LibraryComponent,
    ProductDetailComponent,
    WishlistComponent,
    UserLoginComponent,
    FollowingComponent,
    WishlistOtherComponent,
    LibraryOtherComponent,
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    FormsModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
