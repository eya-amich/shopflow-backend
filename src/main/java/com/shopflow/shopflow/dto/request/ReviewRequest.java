package com.shopflow.shopflow.dto.request;


import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ReviewRequest {

    @NotNull(message = "L'ID du produit est obligatoire")
    private Long productId;

    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note minimum est 1")
    @Max(value = 5, message = "La note maximum est 5")
    private Integer note;

    @Size(max = 1000, message = "Le commentaire ne peut pas dépasser 1000 caractères")
    private String commentaire;
}
