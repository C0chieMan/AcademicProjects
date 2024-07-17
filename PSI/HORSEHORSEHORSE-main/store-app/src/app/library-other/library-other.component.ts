import {Component, Input} from '@angular/core';
import {User} from "../user";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../user.service";
import {ProductService} from "../product.service";

@Component({
  selector: 'app-library-other',
  templateUrl: './library-other.component.html',
  styleUrls: ['../library/library.component.css']
})
export class LibraryOtherComponent {

  @Input() user?: User | null;

  constructor(private router: Router, private userService: UserService, private productService: ProductService,
              private route: ActivatedRoute) {}

  ngOnInit() {
    this.getUser();
  }

  getUser() {
    this.user = this.userService.getUser(String(this.route.snapshot.paramMap.get('username')));
  }

  getItems() {
    if (this.user == undefined) {
      return [];
    }
    return this.user.library.map(item => [this.productService.getProductById(item.productID)?.name,
      item.quantity, item.purchasedAt]);
  }
}
