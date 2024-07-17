import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from "../user";
import { UserService } from "../user.service";

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {
  user: User = {_id: "", username: 'null', password: 'null', imageProfile: 'https://via.placeholder.com/300x200',
    wishlist: [], library: []};
  loggedIn = false;
  isEditingUsername = false;
  newUsername = '';
  isAvatarUpdated = false; // added boolean flag
  isNameUpdated = false;
  isNameWrong = false;
  isNameDuplicate = false;

  avatars = [
    'assets/images/avatar1.jpeg',
    'assets/images/avatar2.jpeg',
    'assets/images/avatar3.jpeg'
  ];

  constructor(private router: Router, private userService: UserService) {}

  ngOnInit() {
    if (this.userService.user != null) {
      this.user = this.userService.user;
      this.loggedIn = true;
    }
    else {
      this.router.navigate(['/login']);
    }
  }

  setAvatar(avatar: string) {
    if (this.loggedIn) {
      this.user.imageProfile = avatar;
      this.userService.updateUser(this.user);
      this.isAvatarUpdated = true; // set flag to true

      setTimeout(() => {
        this.hideAvatarSuccessMessage();
      }, 2000);
    }
    else {
      alert("Error: Not logged in!");
    }
  }

  enableUsernameEditing() {
    if (this.loggedIn) {
      this.isEditingUsername = true;
      this.newUsername = this.user.username;
    }
    else {
      alert("Error: Not logged in!");
    }
  }

  cancelUsernameEditing() {
    this.isEditingUsername = false;
  }

  updateUsername() {
    const alphanumericRegex = /^[a-zA-Z0-9]{3,}$/;
    let user = this.userService.getUser(this.newUsername);
    if (this.loggedIn && (user == null || user == this.user) && this.newUsername.length >= 3 &&
        alphanumericRegex.test(this.newUsername)) {
      this.user.username = this.newUsername;
      this.userService.updateUser(this.user);
      this.isEditingUsername = false;
      this.isNameUpdated = true; // set flag to true
      setTimeout(() => {
        this.hideUserSuccessMessage();
      }, 2000)
    } else if (!this.loggedIn) {
      alert("Error: Not logged in!");
    } else if (user != null) {
      this.isNameDuplicate = true;
      setTimeout(() => {
        this.hideUserDuplicatedMessage();
      }, 3000)
    } else {
      this.isNameWrong = true;
      setTimeout(() => {
        this.hideUserForbiddenMessage();
      }, 3000)
    }
  }

  hideAvatarSuccessMessage() {
    this.isAvatarUpdated = false;
  }

  hideUserSuccessMessage() {
    this.isNameUpdated = false;
  }

  hideUserForbiddenMessage() {
    this.isNameWrong = false;
  }

  hideUserDuplicatedMessage() {
    this.isNameDuplicate = false;
  }

}
