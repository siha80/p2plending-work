import { NgModule }      from '@angular/core';
import { CommonModule }  from '@angular/common';
import { HttpModule }     from '@angular/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgaModule } from '../../theme/nga.module';

import { DropdownModule, ModalModule } from 'ng2-bootstrap/ng2-bootstrap';
import { Ng2SmartTableModule } from 'ng2-smart-table';

import { routing } from './system.routing';
import { System } from './system.componet';
import { BoMenu } from './components/bomenu/bomenu.component';
import { BoMenuService } from './components/bomenu/bomenu.service';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    NgaModule,
    DropdownModule,
    ModalModule,
    routing,
    HttpModule,
    Ng2SmartTableModule
  ],
  declarations: [
    System,
    BoMenu
  ],
  providers: [
    BoMenuService,
    {provide: 'apiUrl', useValue: 'http://localhost:8080/' }
  ]
})
export default class SystemModule {
}
