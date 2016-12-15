import { Injectable, Inject } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';

import { AuthService } from './auth.service';

@Injectable()
export class SecureApi {
  constructor(
    private http: Http,
    private authService: AuthService,
    @Inject('baseUrl') private baseUrl: string) { }

  get(resource: string) {
    let url = "http://localhost:8080/" + resource;
    let token = this.authService.getToken();
    let headerOptions = {
      'Accept': 'application/json',
      'Access-Control-Allow-Credentials': 'true',
      'Authorization': 'Bearer ' + token
    };

    let headers = new Headers(headerOptions);
    let options = new RequestOptions({ headers: headers });

    return this.http.get(url, options)
      .map(response => response.json())
      .catch(this.handleError);
  }

  post(resource: string, jsonParam: any) {
    let url = "http://localhost:8080/" + resource;
    let token = this.authService.getToken();

    let headers = new Headers({ 'Content-Type': 'application/json' });
    headers.append('Access-Control-Allow-Origin', '*');
    headers.append('Authorization', 'Bearer ' + token);
    let options = new RequestOptions({ headers: headers });

    var body = JSON.stringify(jsonParam);

    return this.http.post(url, body, options)
      .map(response => response.json())
      .catch(this.handleError);
  }

  handleError(error: any) {
    console.warn(error);
    return Observable.throw(error.json().message || 'Server error');
  }
}
