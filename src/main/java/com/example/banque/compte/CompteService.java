package com.example.banque.compte;

import com.example.banque.utilisateur.Conseiller;
import com.example.banque.utilisateur.ConseillerRepository;
import com.example.banque.utilisateur.Role;
import com.example.banque.utilisateur.Utilisateur;
import com.example.banque.utilisateur.out.UtilisateurCourant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class CompteService {

    private final CompteRepository comptes;
    private final ConseillerRepository conseillers;
    private final UtilisateurCourant utilisateurCourant;

    public CompteService(CompteRepository comptes, ConseillerRepository conseillers, UtilisateurCourant utilisateurCourant) {
        this.comptes = comptes;
        this.conseillers = conseillers;
        this.utilisateurCourant = utilisateurCourant;
    }

    // Partie 3 : seulement les comptes que l'utilisateur a le droit de voir
    public List<CompteDto> lister() {
        return comptes.findAll().stream()
                .filter(this::peutVoir)
                .map(CompteDto::de)
                .toList();
    }

    public CompteDto trouver(Long id) {
        return CompteDto.de(getCompteAutorise(id));
    }

    // Renvoie le compte si on a le droit de le voir, sinon 404
    // (404 et pas 403 : on ne dit pas que le compte existe)
    public Compte getCompteAutorise(Long id) {
        Compte compte = comptes.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compte " + id + " introuvable"));
        if (!peutVoir(compte)) {
            throw new NoSuchElementException("Compte " + id + " introuvable");
        }
        return compte;
    }

    // Le titulaire, ou un conseiller de la même agence
    public boolean peutVoir(Compte compte) {
        Utilisateur moi = utilisateurCourant.get().orElseThrow();

        if (moi.getRole() == Role.CLIENT) {
            return estTitulaire(compte);
        }

        Conseiller conseiller = conseillers.findByUtilisateurLogin(moi.getLogin()).orElseThrow();
        return conseiller.getAgence().getId().equals(compte.getAgence().getId());
    }

    public boolean estTitulaire(Compte compte) {
        Utilisateur moi = utilisateurCourant.get().orElseThrow();
        return compte.getTitulaire().getUtilisateur().getLogin().equals(moi.getLogin());
    }
}