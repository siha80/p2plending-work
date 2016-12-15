import { Injectable, Inject } from '@angular/core';
import { Http, Headers, Response, RequestOptions } from '@angular/http';
import { AuthService } from '../../core/auth.service';

import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/toPromise';

@Injectable()
export class LoginService{
  public token: string;

  constructor(public http: Http, @Inject('apiUrl') private apiUrl, private authService: AuthService) {
    // set token if saved in local storage
    var currentUser = JSON.parse(localStorage.getItem('currentUser'));
    this.token = currentUser && currentUser.token;
  }

  login(userId, password) {

    let headers = new Headers({ 'Content-Type': 'application/json' });
    headers.append('Access-Control-Allow-Origin', '*');

    let options = new RequestOptions({ headers: headers });

    var body = JSON.stringify({ 'userId': userId, 'password': password });

    return this.http.post(this.apiUrl+'v1/users/login', body, options)
      .toPromise()
      .then((res:Response) => {

        let resJson = res.json();
        let token = resJson && resJson.token;
        let userName = resJson && resJson.userName;

        if (token) {
          this.token = token;
          this.authService.login(userId, token, userName);
        }

        return res.json();
      })
      .catch((res:Response) => {
        return res.json();
      });
  }

  logout(): void {
    // clear token remove user from local storage to log user out
    this.token = null;
    localStorage.removeItem('currentUser');
  }

  addUser(userId) {

    let headers = new Headers({ 'Content-Type': 'application/json' });
    headers.append('Access-Control-Allow-Origin', '*');

    let options = new RequestOptions({ headers: headers });

    var body = JSON.stringify({ 'userId': userId});

    return this.http.post(this.apiUrl+'v1/users/add', body, options)
      .toPromise()
      .then((res:Response) => {
        return res.json();
      })
      .catch((res:Response) => {
        return res.json();
      });
  }

}
