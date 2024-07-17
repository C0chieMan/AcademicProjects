import { Component, Input, OnInit } from '@angular/core';
import { ProductService } from '../product.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from "../user.service";
import { User } from "../user";



@Component({
  selector: 'app-wishlist-other',
  templateUrl: './wishlist-other.component.html',
  styleUrls: ['../wishlist/wishlist.component.css']
})
export class WishlistOtherComponent implements OnInit {

  @Input() user?: User | null;

  constructor(private userService: UserService, private productService: ProductService, private router: Router,
              private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.getUser();
  }

  getUser() {
    this.user = this.userService.getUser(String(this.route.snapshot.paramMap.get('username')));
  }

  hasWishes() {
    if (!this.user) {
      return false;
    }
    return this.user.wishlist.length > 0;
  }

  getProducts() {
    if (this.user == undefined) {
      return [];
    }
    return this.user.wishlist.map(id =>
      this.productService.getProductById(id)).filter(product => product !== undefined);
  }

  viewProductDetails(productId: string) {
    this.router.navigate(['/product', productId]);
  }
}

