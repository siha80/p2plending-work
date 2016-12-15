import { Injectable } from '@angular/core';
import { tokenNotExpired } from 'angular2-jwt';

@Injectable()
export class AuthService {
  private tokenKey: string = 'token';
  private userKey: string = 'userId';
  private userNameKey: string = 'userName';

  constructor() { }

  get isLoggedIn() {
    return tokenNotExpired(null, this.getToken());
  }

  get isTokenExpired() {
    return !tokenNotExpired(null, this.getToken());
  }

  login(userId: string, token: any, userName:string) {
    localStorage.setItem(this.userKey, userId);
    localStorage.setItem(this.userNameKey, userName);
    localStorage.setItem(this.tokenKey, token);
  }

  logout() {
    localStorage.clear();
  }

  getToken() {
    return localStorage.getItem(this.tokenKey);
  }

  get userName(){
    return localStorage.getItem(this.userNameKey);
  }

  get userId() {
    return localStorage.getItem(this.userKey);
  }
}
