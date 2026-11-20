package com.example.banque.utilisateur.out;

import com.example.banque.utilisateur.Utilisateur;
import com.example.banque.utilisateur.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Remplace UtilisateurCourantFake : l'utilisateur courant est celui connecté en Basic Auth
@Component
public class UtilisateurCourantImpl implements UtilisateurCourant {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurCourantImpl(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public Optional<Utilisateur> get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return Optional.empty();
        }
        // auth.getName() = le login
        return utilisateurRepository.findByLogin(auth.getName());
    }
}
