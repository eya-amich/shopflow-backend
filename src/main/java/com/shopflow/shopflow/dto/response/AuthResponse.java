package com.shopflow.shopflow.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String email;
    private String prenom;
    private String nom;
    private String role;
}
