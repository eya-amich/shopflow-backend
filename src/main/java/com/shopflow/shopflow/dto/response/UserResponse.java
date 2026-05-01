package com.shopflow.shopflow.dto.response;


import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String prenom;
    private String nom;
    private String role;
    private boolean actif;
    private LocalDateTime dateCreation;

    // Si SELLER
    private String nomBoutique;
    private String descriptionBoutique;
    private String logoBoutique;
    private Double noteBoutique;
}
