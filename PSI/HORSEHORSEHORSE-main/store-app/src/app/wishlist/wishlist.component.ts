import { Component, OnInit } from '@angular/core';
import { Product } from '../product';
import { ProductService } from '../product.service';
import { Router } from '@angular/router';
import {UserService} from "../user.service";
import {User} from "../user";



@Component({
  selector: 'app-wishlist',
  templateUrl: './wishlist.component.html',
  styleUrls: ['./wishlist.component.css']
})
export class WishlistComponent implements OnInit {

  user: User = {_id: "", username: 'null', password: 'null', imageProfile: 'https://via.placeholder.com/300x200',
    wishlist: [], library: []};

  public successMessage: string = '';

  constructor(private userService: UserService, private productService: ProductService, private router: Router) {}

  ngOnInit(): void {
    if (this.userService.user != null) {
      this.user = this.userService.user;
    }
    else {
      this.router.navigate(['/login']);
    }
  }

  getProducts() {
    return this.user.wishlist.map(id =>
      this.productService.getProductById(id)).filter(product => product !== undefined);
  }

  removeItemFromWishlist(item: Product) {
    const index = this.user.wishlist.findIndex(p => p === item._id);
    if (index !== -1) {
      this.user.wishlist.splice(index, 1);
    }
    this.userService.updateUser(this.user);
    this.successMessage = 'The product was removed successfully';
    setTimeout(() => {
      this.successMessage = '';
    }, 2000);
  }

  viewProductDetails(productId: string) {
    this.router.navigate(['/product', productId]);
  }

}

