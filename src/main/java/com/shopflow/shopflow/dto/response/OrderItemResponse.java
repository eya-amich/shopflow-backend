package com.shopflow.shopflow.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long id;
    private Long productId;
    private String productNom;
    private String productImage;
    private String variantAttribut;
    private String variantValeur;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal total;
}
