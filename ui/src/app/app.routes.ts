import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'comptes' },
  {
    path: 'login',
    loadComponent: () => import('./features/login/login').then((m) => m.Login),
    title: 'Banque · connexion',
  },
  {
    path: 'comptes',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['CLIENT', 'CONSEILLER'] },
    loadComponent: () => import('./features/comptes/liste-comptes').then((m) => m.ListeComptes),
    title: 'Banque · comptes',
  },
  {
    path: 'comptes/:id',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['CLIENT', 'CONSEILLER'] },
    loadComponent: () => import('./features/comptes/detail-compte').then((m) => m.DetailCompte),
    title: 'Banque · compte',
  },
  { path: '**', redirectTo: 'comptes' },
];