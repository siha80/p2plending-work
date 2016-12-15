import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'logout-component',
  template: ''
})
export class LogoutComponent implements OnInit {
  constructor(
    private router: Router,
    private authService: AuthService) { }

  ngOnInit() {
    this.authService.logout();
    this.router.navigate(['login']);
  }
}
