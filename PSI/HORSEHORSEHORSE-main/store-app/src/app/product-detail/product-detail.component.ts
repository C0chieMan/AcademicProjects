import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Product } from '../product';
import { ProductService } from '../product.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {

  public product!: Product;
  public productType!: string;
  public description!: string;
  public platform!: string;
  public languages!: string;
  public price!: number;
  public rating!: string;
  public mainImage!: string;
  public video!: string;


  constructor(private route: ActivatedRoute, private productService: ProductService, private router: Router) { }

  ngOnInit(): void {
    const productId = String(this.route.snapshot.paramMap.get('id'));
    const product = this.productService.getProductById(productId);
    if (product) {
      this.product = product;
    }
    this.productType = this.product.tipo;
    this.description = this.product.description;
    this.platform = this.product.plataform;
    this.languages = this.product.idioma;
    this.price = this.product.price;
    this.rating = this.product.rate;
    this.mainImage = this.product.imageUrl;
    this.video = this.product.youtubeURL;
  }

}
