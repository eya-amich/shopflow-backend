package com.shopflow.shopflow.dto.request;


import com.shopflow.shopflow.enums.Role;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
            message = "Le mot de passe doit contenir une majuscule, une minuscule, un chiffre et un caractère spécial")
    private String motDePasse;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role; // CUSTOMER ou SELLER

    // Champs optionnels pour SELLER
    private String nomBoutique;
    private String descriptionBoutique;
    private String logoBoutique;
}
