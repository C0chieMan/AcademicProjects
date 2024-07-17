import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from "../user";
import { UserService } from "../user.service";
import {ProductService} from "../product.service";

@Component({
  selector: 'app-library',
  templateUrl: './library.component.html',
  styleUrls: ['./library.component.css']
})
export class LibraryComponent implements OnInit {

  user: User = {_id: "", username: 'null', password: 'null', imageProfile: 'https://via.placeholder.com/300x200',
    wishlist: [], library: []};

  constructor(private router: Router, private userService: UserService, private productService: ProductService) {}

  ngOnInit() {
    if (this.userService.user != null) {
      this.user = this.userService.user;
    }
    else {
      this.router.navigate(['/login']);
    }
  }

  getItems() {
    return this.user.library.map(item => [this.productService.getProductById(item.productID)?.name,
      item.quantity, item.purchasedAt]);
  }
}
