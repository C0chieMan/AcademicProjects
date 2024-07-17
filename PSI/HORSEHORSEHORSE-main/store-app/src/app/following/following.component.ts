import {Component, OnInit} from '@angular/core';
import { Router } from "@angular/router";
import { UserService } from "../user.service";
import { User } from "../user";

@Component({
  selector: 'app-following',
  templateUrl: './following.component.html',
  styleUrls: ['./following.component.css']
})
export class FollowingComponent implements OnInit{

  user: User = {_id: "", username: 'null', password: 'null', imageProfile: 'https://via.placeholder.com/300x200',
    wishlist: [], library: []};

  filteredUsers: User[] = [];

  public searchTerm = '';

  constructor(private router: Router, private userService: UserService) { }

  ngOnInit(): void {
    if (this.userService.user != null) {
      this.user = this.userService.user;
    }
    else {
      this.router.navigate(['/login']);
    }
    this.filteredUsers = [...this.userService.users];
    this.filteredUsers.splice(this.filteredUsers.indexOf(this.user), 1);
  }

  searchUsers() {
    this.filteredUsers = this.userService.users.filter(user => {
      return user != this.user && user.username.includes(this.searchTerm);
    });
  }
}
