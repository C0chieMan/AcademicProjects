import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private usersUrl = 'http://localhost:3074';

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  user: User | null = null;

  users: User[] = [];

  constructor(private http: HttpClient) { }

  changeUser(user: User) {
    this.user = user;
  }

  getUser(username: String): User | null {
    let user = null;
    for (let i = 0; i < this.users.length; i++) {
      if (this.users[i].username == username) {
        user = this.users[i];
      }
    }
    return user;
  }

  getUsers() {
    this.http.get<User[]>(this.usersUrl + "/getUsers", this.httpOptions)
      .subscribe(res => this.users = res);
  }

  addUser(user: User) {
    this.http.post<User>(this.usersUrl + "/postUser", user, this.httpOptions)
      .subscribe(user => this.users.push(user));
    setTimeout(() => {
      this.getUsers();
    }, 1000);
  }

  updateUser(user: User) {
    this.http.put(this.usersUrl + "/putUser/" + user._id, user, this.httpOptions)
      .subscribe();
    setTimeout(() => {
      this.getUsers();
    }, 1000);
  }
}

