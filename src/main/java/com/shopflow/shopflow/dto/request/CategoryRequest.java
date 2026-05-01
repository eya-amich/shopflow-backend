package com.shopflow.shopflow.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    private String nom;

    private String description;

    private Long parentId; // null si catégorie racine
}
