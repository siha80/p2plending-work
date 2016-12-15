import { NgModule }      from '@angular/core';
import { CommonModule }  from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgaModule } from '../../theme/nga.module';

import { routing }       from './report.routing';
import { DropdownModule, ModalModule } from 'ng2-bootstrap/ng2-bootstrap';
import { Report } from './report.component';
import { Report1 } from './components/report1/report1.component';
import { Report2 } from './components/report2/report2.component';
import { Report3 } from './components/report3/report3.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgaModule,
    DropdownModule,
    ModalModule,
    routing
  ],
  declarations: [
    Report,
    Report1,
    Report2,
    Report3
  ],
  providers: [
    //IconsService
  ]
})
export default class OpertaionModule {
}
