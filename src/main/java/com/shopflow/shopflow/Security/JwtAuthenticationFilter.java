package com.shopflow.shopflow.Security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Récupérer le header Authorization
        final String authHeader = request.getHeader("Authorization");

        // 2. Vérifier si le header contient un Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Pas de token → on laisse passer (les endpoints publics fonctionneront)
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraire le token (enlever "Bearer ")
        final String jwt = authHeader.substring(7);

        try {
            // 4. Extraire l'email du token
            final String email = jwtService.extractEmail(jwt);

            // 5. Si l'email existe et qu'il n'y a pas déjà une authentification
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Charger l'utilisateur depuis la base
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                // 7. Vérifier si le token est valide
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    // 8. Créer l'objet d'authentification
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 9. Mettre l'authentification dans le SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token invalide ou expiré → on ne fait rien, la requête sera non authentifiée
            logger.error("Erreur d'authentification JWT : " + e.getMessage());
        }

        // 10. Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
