import { Injectable, Inject } from '@angular/core';
import { Http, Headers, Response, RequestOptions } from '@angular/http';
import {SecureApi} from '../../../../core/secure.api'

import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/toPromise';

@Injectable()
export class BoMenuService{
  public token: string;

  constructor(public http: Http, @Inject('apiUrl') private apiUrl, private _secureApi:SecureApi ){
    // set token if saved in local storage
    var currentUser = JSON.parse(localStorage.getItem('currentUser'));
    this.token = currentUser && currentUser.token;
  }

  getMenuList(param) {
    return this._secureApi.post('v1/menu/system/list', param)
      .toPromise()
      .then((res:Response) => {
        return res["menuList"];
      })
      .catch((res:Response) => {
        return res.json();
      });
  }
}
