import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { API } from '../api/banque';

export interface Utilisateur {
  readonly login: string;
  readonly roles: readonly string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  readonly utilisateur = signal<Utilisateur | null>(null);

  // L'en-tête "Basic ..." gardé en mémoire (perdu si on recharge la page)
  entete: string | null = null;

  async connecter(login: string, motDePasse: string): Promise<void> {
    const entete = 'Basic ' + btoa(login + ':' + motDePasse);

    // Si le mot de passe est faux, le serveur répond 401 et ça lance une erreur
    const moi = await firstValueFrom(
      this.http.get<Utilisateur>(API + '/me', { headers: { Authorization: entete } }),
    );

    this.entete = entete;
    this.utilisateur.set(moi);
  }

  deconnecter(): void {
    this.entete = null;
    this.utilisateur.set(null);
  }
}