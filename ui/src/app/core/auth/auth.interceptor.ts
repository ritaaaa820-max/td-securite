import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { API } from '../api/banque';
import { AuthService } from './auth.service';

// Ajoute l'en-tête Authorization aux requêtes vers l'API
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  // Pas notre API, ou requête de connexion (qui a déjà son en-tête) : on ne touche à rien
  if (!req.url.startsWith(API) || req.headers.has('Authorization')) {
    return next(req);
  }

  if (auth.entete) {
    req = req.clone({ setHeaders: { Authorization: auth.entete } });
  }

  return next(req).pipe(
    catchError((err) => {
      // 401 : on n'est plus connecté -> page de login
      if (err instanceof HttpErrorResponse && err.status === 401) {
        auth.deconnecter();
        router.navigate(['/login']);
      }
      return throwError(() => err);
    }),
  );
};