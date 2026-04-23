import { Routes } from '@angular/router';
import { FullComponent } from './layouts/full/full.component';
import { BlankComponent } from './layouts/blank/blank.component';
import { HomeComponent } from './public/home/home.component';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [

  // ⚡ Cuando entras a '/', manda al HOME
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },

  // 1️⃣ HOME PÚBLICO
  {
    path: 'home',
    component: HomeComponent,
  },

  // 2️⃣ LOGIN / REGISTER
  {
    path: 'auth',
    component: BlankComponent,
    children: [
      {
        path: '',
        loadChildren: () =>
          import('./auth/authentication/authentication.routes')
            .then((m) => m.AuthenticationRoutes),
      },
    ],
  },

  // 3️⃣ ZONA PRIVADA (protegida)
  {
    path: 'app',
    component: FullComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        loadChildren: () =>
          import('./pages/pages.routes').then((m) => m.PagesRoutes),
      },
      {
        path: 'ui-components',
        loadChildren: () =>
          import('./pages/ui-components/ui-components.routes')
            .then((m) => m.UiComponentsRoutes),
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
    ],
  },

  // 4️⃣ NOT FOUND → HOME
  {
    path: '**',
    redirectTo: 'home',
  },
];

