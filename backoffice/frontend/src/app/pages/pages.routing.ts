import { Routes, RouterModule }  from '@angular/router';
import { Pages } from './pages.component';
import { AuthGuard } from '../core/auth.guard';
// noinspection TypeScriptValidateTypes
const routes: Routes = [
  {
    path: 'login',
    loadChildren: () => System.import('./login/login.module')
  },
  {
    path: 'register',
    loadChildren: () => System.import('./register/register.module')
  },
  {
    path: 'pages',
    component: Pages,
    canActivate:[AuthGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadChildren: () => System.import('./dashboard/dashboard.module') },
      { path: 'report', loadChildren: () => System.import('./report/report.module') },
      { path: 'operation', loadChildren: () => System.import('./operation/operation.module') },
      { path: 'system', loadChildren: () => System.import('./system/system.module') },
      { path: 'editors', loadChildren: () => System.import('./editors/editors.module') },
      //{ path: 'components', loadChildren: () => System.import('./components/components.module') }
      { path: 'charts', loadChildren: () => System.import('./charts/charts.module') },
      { path: 'ui', loadChildren: () => System.import('./ui/ui.module') },
      { path: 'forms', loadChildren: () => System.import('./forms/forms.module') },
      { path: 'tables', loadChildren: () => System.import('./tables/tables.module') },
      { path: 'maps', loadChildren: () => System.import('./maps/maps.module') }
    ]
  }
];

export const routing = RouterModule.forChild(routes);
