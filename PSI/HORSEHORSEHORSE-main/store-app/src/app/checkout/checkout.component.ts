import {Component, OnInit} from '@angular/core';
import { CartService } from '../cart-service.service';
import { Router } from '@angular/router';
import { User } from "../user";
import { UserService } from "../user.service";
import { ProductService } from "../product.service";

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {

  user: User = {_id: "", username: 'null', password: 'null', imageProfile: 'https://via.placeholder.com/300x200',
    wishlist: [], library: []};

  total: number;

  submitted = false;

  success = false;

  constructor(public cartService: CartService, private router: Router, private userService: UserService,
              public productService: ProductService) {
    this.total = cartService.getTotal();
  }

  ngOnInit(): void {
    if (this.userService.user != null) {
      this.user = this.userService.user;
    }
    else {
      this.router.navigate(['/login']);
    }
  }

  onSubmit() {
    this.success = Math.random() < 0.5;
    this.submitted = true;
    if (this.success) {
      this.moveItemsToLibrary();
    }
  }

  moveItemsToLibrary() {
    const items = this.cartService.getItems();
    const purchasedItems = items.map(item => ({productID: item.product._id, quantity: item.quantity,
      purchasedAt: new Date()}));
    this.user.library.push(...purchasedItems);
    this.userService.updateUser(this.user);
    this.cartService.clearCart();
  }
}



