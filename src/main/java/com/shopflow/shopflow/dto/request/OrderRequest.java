package com.shopflow.shopflow.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderRequest {

    @NotNull(message = "L'ID de l'adresse de livraison est obligatoire")
    private Long addressId;
}
