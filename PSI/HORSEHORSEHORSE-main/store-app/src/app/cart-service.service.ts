import { Injectable } from '@angular/core';
import { Product } from './product';
import { ProductService } from "./product.service";

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private items: { product: Product, quantity: number }[] = [];

  constructor(public productService: ProductService) { }

  addItem(product: Product, quantity: number = 1) {
    const index = this.items.findIndex(item => item.product === product);
    if (index !== -1) {
      this.items[index].quantity += quantity;
    } else {
      this.items.push({ product, quantity });
    }
  }

  removeItem(product: Product) {
    const index = this.items.findIndex(item => item.product === product);
    if (index !== -1) {
      this.items.splice(index, 1);
    }
  }

  updateQuantity(product: Product, quantity: number) {
    const index = this.items.findIndex(item => item.product === product);
    if (index !== -1) {
      this.items[index].quantity = quantity;
    }
  }

  getItems() {
    return this.items;
  }

  clearCart() {
    this.items = [];
    return this.items;
  }

  getTotal() {
    return this.items.reduce((acc, item) =>
      acc + item.product.price * item.quantity, 0);
  }

}
