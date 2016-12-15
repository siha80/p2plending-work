import { NgModule }      from '@angular/core';
import { CommonModule }  from '@angular/common';
import { HttpModule }     from '@angular/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgaModule } from '../../theme/nga.module';

import { Login } from './login.component';
import { routing }       from './login.routing';
import { LoginService } from "./login.service";


@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    NgaModule,
    routing,
    HttpModule
  ],
  declarations: [
    Login
  ],
  providers: [
    LoginService,
    {provide: 'apiUrl', useValue: 'http://localhost:8080/' }
  ]
})
export default class LoginModule {}
