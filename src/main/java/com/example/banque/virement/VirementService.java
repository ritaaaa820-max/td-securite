package com.example.banque.virement;

import com.example.banque.compte.Compte;
import com.example.banque.compte.CompteService;
import com.example.banque.utilisateur.out.UtilisateurCourant;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class VirementService {

    private static final int TAILLE_PAGE = 20;

    private final VirementRepository virements;
    private final CompteService compteService;
    private final UtilisateurCourant utilisateurCourant;

    public VirementService(VirementRepository virements, CompteService compteService, UtilisateurCourant utilisateurCourant) {
        this.virements = virements;
        this.compteService = compteService;
        this.utilisateurCourant = utilisateurCourant;
    }

    @Transactional(readOnly = true)
    public List<VirementDto> lister(Long compteId, int page) {
        // Partie 3 : on vérifie d'abord qu'on a le droit de voir ce compte (sinon 404)
        compteService.getCompteAutorise(compteId);

        var pageable = PageRequest.of(page, TAILLE_PAGE, Sort.by(Sort.Direction.DESC, "id"));
        return virements.findByCompteId(compteId, pageable).stream().map(VirementDto::de).toList();
    }

    public VirementDto creer(NouveauVirement demande) {
        Compte compte = compteService.getCompteAutorise(demande.compteId());

        // Partie 3 : seul le titulaire du compte peut faire un virement (un conseiller non)
        if (!compteService.estTitulaire(compte)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        var virement = new Virement(compte, demande.ibanDestinataire(), demande.montant());
        return VirementDto.de(virements.save(virement));
    }

    // Le rôle CONSEILLER est vérifié dans SecuriteConfig
    public VirementDto valider(Long id) {
        Virement virement = virements.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Virement " + id + " introuvable"));
        String login = utilisateurCourant.get()
                .orElseThrow(() -> new IllegalStateException("Personne n'est connecté"))
                .getLogin();
        virement.valider(login);
        return VirementDto.de(virement);
    }
}