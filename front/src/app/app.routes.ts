import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'home',
    loadChildren: () =>
      import('./pages/home/home.routes').then((m) => m.routes),
  },
  {
    path: 'shoping',
    loadChildren: () =>
      import('./pages/shoping/shoping.routes').then((m) => m.routes),
  },
  {
    path: 'user',
    loadChildren: () =>
      import('./pages/user/user.routes').then((m) => m.routes),
  },
  {
    path: 'admin',
    loadChildren: () =>
      import('./pages/admin/admin.routes').then((m) => m.routes),
  },

  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
];
