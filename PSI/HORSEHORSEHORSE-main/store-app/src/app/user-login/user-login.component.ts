import { UserService } from "../user.service";
import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { User } from "../user";
import { Product } from "../product";

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})
export class UserLoginComponent implements OnInit {

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit() {
    this.userService.getUsers();
  }

  login(username : String, pwd : String) {
    if (username == "") {
      alert("Username is required!");
    }
    else if (pwd == "") {
      alert("Password is required!");
    }
    else {
      let user = this.userService.getUser(username);
      if (user != null && user.password == pwd) {
        this.userService.changeUser(user);
        this.router.navigate(['/store']);
      }
      else {
        alert("Wrong username or password!");
      }
    }
  }

  register(username: string, pwd: string) {
    const usernameRegex = /^[a-zA-Z0-9]{3,}$/;
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/;
    if (username == "") {
      alert("Username is required!");
    }
    else if (pwd == "") {
      alert("Password is required!");
    }
    else if (!usernameRegex.test(username)) {
      alert("Username must have at least 3 alphanumeric characters!");
    }
    else if (!passwordRegex.test(pwd)) {
      alert("Password must have at least 8 characters, with at least one uppercase letter, " +
        "one lowercase letter and one number!");
    }
    else {
      let user = this.userService.getUser(username);
      if (user != null) {
        alert("Username already exists!");
      }
      else {
        this.userService.addUser({ username: username, password: pwd,
          imageProfile: 'assets/images/avatar1.jpeg'} as User);
        alert("Registration successful!\nPlease login!");
      }
    }
  }

}
