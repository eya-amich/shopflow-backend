package com.shopflow.shopflow.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductVariantRequest {

    @NotBlank(message = "L'attribut est obligatoire")
    private String attribut;

    @NotBlank(message = "La valeur est obligatoire")
    private String valeur;

    private Integer stockSupplementaire;

    private BigDecimal prixDelta;
}
