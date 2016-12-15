import { Routes, RouterModule }  from '@angular/router';

import { Operation } from './operation.component';
import { Operation1 } from './components/operation1/operation1.component';
import { Operation2 } from './components/operation2/operation2.component';
import { Operation3 } from './components/operation3/operation3.component';
import { Operation4 } from './components/operation4/operation4.component';
import { Operation5 } from './components/operation5/operation5.component';


// noinspection TypeScriptValidateTypes
const routes: Routes = [
  {
    path: '',
    component: Operation,
    children: [
      { path: 'operation1', component: Operation1 },
      { path: 'operation2', component: Operation2 },
      { path: 'operation3', component: Operation3 },
      { path: 'operation4', component: Operation4 },
      { path: 'operation5', component: Operation5 }
    ]
  }
];

export const routing = RouterModule.forChild(routes);
