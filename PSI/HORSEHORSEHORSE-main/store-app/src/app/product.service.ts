import { Injectable, OnInit } from '@angular/core';
import { Product } from './product';
import { HttpClient, HttpHeaders } from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private productsUrl = 'http://localhost:3074';

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  products: Product[] = [];

  constructor(private http: HttpClient) {
    this.http.get<Product[]>(this.productsUrl + "/getProducts", this.httpOptions)
      .subscribe(res => this.products = res);
  }

  getProducts(): Product[] {
    return this.products;
  }

  getProductById(id: string): Product | undefined {
    return this.products.find(p => p._id === id);
  }

}
