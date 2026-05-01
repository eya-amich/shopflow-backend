package com.shopflow.shopflow.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CartItemRequest {

    @NotNull(message = "L'ID du produit est obligatoire")
    private Long productId;

    private Long variantId; // nullable

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité minimum est 1")
    private Integer quantite;
}
