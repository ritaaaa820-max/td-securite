import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

// Partie 2 : il faut être connecté, sinon on va sur /login?retour=<url demandée>
export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.utilisateur()) {
    return true;
  }
  return router.createUrlTree(['/login'], { queryParams: { retour: state.url } });
};

// Partie 3 : il faut avoir un des rôles de data.roles
export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const roles: string[] = route.data['roles'];
  const mesRoles = auth.utilisateur()?.roles ?? [];

  if (roles.some((role) => mesRoles.includes(role))) {
    return true;
  }
  return router.createUrlTree(['/login']);
};