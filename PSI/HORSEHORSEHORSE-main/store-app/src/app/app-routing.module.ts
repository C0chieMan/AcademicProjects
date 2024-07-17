import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StoreComponent } from './store/store.component';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { CheckoutComponent } from './checkout/checkout.component';
import { LibraryComponent } from './library/library.component';
import { ProductDetailComponent } from './product-detail/product-detail.component';
import { WishlistComponent } from './wishlist/wishlist.component';
import { UserLoginComponent } from "./user-login/user-login.component";
import { FollowingComponent } from "./following/following.component";
import { WishlistOtherComponent } from "./wishlist-other/wishlist-other.component";
import { LibraryOtherComponent } from "./library-other/library-other.component";

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: UserLoginComponent},
  { path: 'store', component: StoreComponent },
  { path: 'profile', component: UserProfileComponent },
  { path: 'checkout', component: CheckoutComponent },
  { path: 'library', component: LibraryComponent},
  { path: 'library/:username', component: LibraryOtherComponent },
  { path: 'product/:id', component: ProductDetailComponent},
  { path: 'wishlist', component: WishlistComponent },
  { path: 'wishlist/:username', component: WishlistOtherComponent },
  { path: 'following', component: FollowingComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
