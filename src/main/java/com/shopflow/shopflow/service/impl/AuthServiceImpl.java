package com.shopflow.shopflow.service.impl;

import com.shopflow. shopflow. dto.request.LoginRequest;
import com.shopflow. shopflow. dto.request.RefreshTokenRequest;
import com.shopflow. shopflow. dto.request.RegisterRequest;
import com.shopflow. shopflow. dto.response.AuthResponse;
import com.shopflow. shopflow. entity.Cart;
import com.shopflow. shopflow.entity.SellerProfile;
import com.shopflow.shopflow. entity.User;
import com.shopflow.shopflow. enums.Role;
import com.shopflow.shopflow. exception.BadRequestException;
import com.shopflow.shopflow. exception.DuplicateResourceException;
import com.shopflow.shopflow. exception.UnauthorizedException;
import com.shopflow.shopflow. repository.CartRepository;
import com.shopflow. shopflow. repository.UserRepository;
import com.shopflow.shopflow.Security.CustomUserDetailsService;
import com.shopflow. shopflow. Security.JwtService;
import com.shopflow.shopflow. service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Vérifier email unique
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Cet email est déjà utilisé");
        }

        // Interdire la création de comptes ADMIN via l'inscription
        //if (request.getRole() == Role.ADMIN) {
           // throw new BadRequestException("Impossible de créer un compte administrateur via l'inscription");
       // }


        // Créer l'utilisateur
        User user = User.builder()
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .prenom(request.getPrenom())
                .nom(request.getNom())
                .role(request.getRole())
                .actif(true)
                .build();

        // Si SELLER, créer le profil boutique
        if (request.getRole() == Role.SELLER) {
            if (request.getNomBoutique() == null || request.getNomBoutique().isBlank()) {
                throw new BadRequestException("Le nom de boutique est obligatoire pour un vendeur");
            }
            SellerProfile profile = SellerProfile.builder()
                    .user(user)
                    .nomBoutique(request.getNomBoutique())
                    .description(request.getDescriptionBoutique())
                    .logo(request.getLogoBoutique())
                    .build();
            user.setSellerProfile(profile);
        }

        user = userRepository.save(user);

        // Si CUSTOMER, créer un panier vide
        if (request.getRole() == Role.CUSTOMER) {
            Cart cart = Cart.builder()
                    .customer(user)
                    .build();
            cartRepository.save(cart);
        }

        // Générer les tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Sauvegarder le refresh token
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .prenom(user.getPrenom())
                .nom(user.getNom())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Authentifier avec Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
        );

        // Charger l'utilisateur
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Email ou mot de passe incorrect"));

        // Générer les tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Sauvegarder le refresh token
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .prenom(user.getPrenom())
                .nom(user.getNom())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        // Trouver l'utilisateur par refresh token
        User user = userRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token invalide"));

        // Vérifier que le token n'est pas expiré
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        if (!jwtService.isTokenValid(request.getRefreshToken(), userDetails)) {
            throw new UnauthorizedException("Refresh token expiré");
        }

        // Générer un nouveau access token
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .email(user.getEmail())
                .prenom(user.getPrenom())
                .nom(user.getNom())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));
        user.setRefreshToken(null);
        userRepository.save(user);
    }
}
