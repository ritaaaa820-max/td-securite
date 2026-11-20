package com.example.banque.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
public class SecuriteConfig {

    // Compare le mot de passe envoyé avec le hash BCrypt en base
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> {});           // utilise le CorsConfig
        http.csrf(csrf -> csrf.disable()); // pas de session ni de cookie -> pas besoin de CSRF
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Basic Auth. On renvoie juste 401 (sans WWW-Authenticate) pour que le navigateur
        // n'ouvre pas sa popup de connexion.
        http.httpBasic(basic -> basic.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // preflight CORS
                .requestMatchers(HttpMethod.POST, "/api/virements/*/valider").hasRole("CONSEILLER") // partie 3
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll());

        return http.build();
    }
}