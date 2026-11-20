import { CurrencyPipe, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject, input, signal, viewChild } from '@angular/core';
import { FormControl, FormGroup, FormGroupDirective, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { Banque, Compte, Virement } from '../../core/api/banque';
import { AuthService } from '../../core/auth/auth.service'; // partie 3

@Component({
  selector: 'app-detail-compte',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    CurrencyPipe,
    DatePipe,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressBarModule,
    MatTableModule,
  ],
  styleUrl: './detail-compte.scss',
  templateUrl: './detail-compte.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailCompte implements OnInit {
  private readonly banque = inject(Banque);
  protected readonly auth = inject(AuthService); // partie 3 : pour cacher le bouton Valider

  /** Le `:id` de la route (withComponentInputBinding). */
  readonly id = input.required<string>();

  protected readonly compte = signal<Compte | null>(null);
  protected readonly virements = signal<Virement[]>([]);
  protected readonly erreur = signal<string | null>(null);

  /** Pour remettre le formulaire à neuf, sans erreurs « champ requis » après l'envoi. */
  private readonly saisie = viewChild(FormGroupDirective);

  protected readonly colonnes = ['date', 'destinataire', 'montant', 'statut', 'actions'];

  protected readonly formulaire = new FormGroup({
    ibanDestinataire: new FormControl('', { nonNullable: true, validators: Validators.required }),
    montant: new FormControl<number | null>(null, [Validators.required, Validators.min(0.01)]),
  });

  async ngOnInit(): Promise<void> {
    try {
      const id = Number(this.id());
      this.compte.set(await this.banque.compte(id));
      this.virements.set(await this.banque.virements(id));
    } catch {
      this.erreur.set('Impossible de charger ce compte.');
    }
  }

  protected async virer(): Promise<void> {
    const compte = this.compte();
    if (!compte || this.formulaire.invalid) {
      return;
    }
    const { ibanDestinataire, montant } = this.formulaire.getRawValue();
    try {
      await this.banque.creerVirement({
        compteId: compte.id,
        ibanDestinataire,
        montant: Math.round((montant ?? 0) * 100),
      });
      this.saisie()?.resetForm();
      this.virements.set(await this.banque.virements(compte.id));
    } catch {
      this.erreur.set('Le virement a été refusé.');
    }
  }

  protected async valider(virement: Virement): Promise<void> {
    try {
      await this.banque.validerVirement(virement.id);
      this.virements.set(await this.banque.virements(virement.compteId));
    } catch {
      this.erreur.set('La validation a été refusée.');
    }
  }
}