import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {UserService} from "./user.service";
import {CartService} from "./cart-service.service";
import {ProductService} from "./product.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'store-app';

  constructor(public router: Router) {
  }

  goToProfile() {
    this.router.navigate(['/profile']);
  }

  goToStore() {
    this.router.navigate(['/store']);
  }

  goToLibrary() {
    this.router.navigate(['/library']);
  }

  goToWishlist() {
    this.router.navigate(['/wishlist']);
  }

  goToFollowing() {
    this.router.navigate(['/following']);
  }

}
