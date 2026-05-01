package com.shopflow.shopflow.dto.response;


import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductVariantResponse {

    private Long id;
    private String attribut;
    private String valeur;
    private Integer stockSupplementaire;
    private BigDecimal prixDelta;
}
