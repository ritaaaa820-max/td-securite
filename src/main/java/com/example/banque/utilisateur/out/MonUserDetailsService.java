package com.example.banque.utilisateur.out;

import com.example.banque.utilisateur.Utilisateur;
import com.example.banque.utilisateur.UtilisateurRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security l'appelle pour trouver l'utilisateur qui se connecte
@Service
public class MonUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public MonUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Utilisateur u = utilisateurRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur inconnu : " + login));

        return User.withUsername(u.getLogin())
                .password(u.getMotDePasse())  // le hash BCrypt
                .roles(u.getRole().name())    // CLIENT -> ROLE_CLIENT
                .build();
    }
}
