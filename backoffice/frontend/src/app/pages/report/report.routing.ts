import { Routes, RouterModule }  from '@angular/router';

import { Report } from './report.component';
import { Report1 } from './components/report1/report1.component';
import { Report2 } from './components/report2/report2.component';
import { Report3 } from './components/report3/report3.component';


// noinspection TypeScriptValidateTypes
const routes: Routes = [
  {
    path: '',
    component: Report,
    children: [
      { path: 'report1', component: Report1 },
      { path: 'report2', component: Report2 },
      { path: 'report3', component: Report3 }
    ]
  }
];

export const routing = RouterModule.forChild(routes);
