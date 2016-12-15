import { Routes, RouterModule }  from '@angular/router';

import { System } from './system.componet';
import { BoMenu } from './components/bomenu/bomenu.component';

// noinspection TypeScriptValidateTypes
const routes: Routes = [
  {
    path: '',
    component: System,
    children: [
      { path: 'bomenu', component: BoMenu }
    ]
  }
];

export const routing = RouterModule.forChild(routes);
