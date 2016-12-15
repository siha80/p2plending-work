import { NgModule }      from '@angular/core';
import { CommonModule }  from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgaModule } from '../../theme/nga.module';

import { routing }       from './operation.routing';
import { DropdownModule, ModalModule } from 'ng2-bootstrap/ng2-bootstrap';
import { Operation } from './operation.component';
import { Operation1 } from './components/operation1/operation1.component';
import { Operation2 } from './components/operation2/operation2.component';
import { Operation3 } from './components/operation3/operation3.component';
import { Operation4 } from './components/operation4/operation4.component';
import { Operation5 } from './components/operation5/operation5.component';

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
    Operation,
    Operation1,
    Operation2,
    Operation3,
    Operation4,
    Operation5
  ],
  providers: [
    //IconsService
  ]
})
export default class OpertaionModule {
}
