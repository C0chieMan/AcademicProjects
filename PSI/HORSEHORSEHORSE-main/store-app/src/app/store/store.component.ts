import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartService } from '../cart-service.service';
import { ProductService } from '../product.service';
import { Product } from '../product';
import { User } from "../user";
import { UserService } from "../user.service";

@Component({
  selector: 'app-store',
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.css']
})

export class StoreComponent implements OnInit {

  user: User = {_id: "", username: 'null', password: 'null', imageProfile: 'https://via.placeholder.com/300x200',
    wishlist: [], library: []};

  public products: Product[] = [];

  public filteredProducts: Product[] = [];

  public searchTerm = '';

  public message: string = '';

  public successMessage: string = '';

  showTooltip: boolean = false;

  constructor(private router: Router, private userService: UserService, public cartService: CartService,
              public productService: ProductService) {
    this.products = this.productService.getProducts();
    this.filteredProducts = [...this.products];
    if (this.products.length == 0) {
      setTimeout(() => {
        this.getProducts();
      }, 2000);
    }
  }

  getProducts() {
    this.products = this.productService.getProducts();
    this.filteredProducts = [...this.products];
    if (this.products.length == 0) {
      setTimeout(() => {
        this.getProducts();
      }, 2000);
    }
  }

  ngOnInit(): void {
    if (this.userService.user != null) {
      this.user = this.userService.user;
    }
    else {
      this.router.navigate(['/login']);
    }
  }

  viewDetails(productId: string) {
    this.router.navigate(['/product', productId]);
  }

  filterProducts() {
    this.filteredProducts = this.products.filter(product => {
      return product.name.toLowerCase().includes(this.searchTerm.toLowerCase());
    });
  }

  addToCart(product: Product) {
    this.cartService.addItem(product);
  }

  addToWishList(product: Product) {
    this.user.wishlist.push(product._id);
    this.userService.updateUser(this.user);
    this.successMessage = 'The product was added successfully';
    setTimeout(() => {
      this.successMessage = '';
    }, 2000);
  }
}

