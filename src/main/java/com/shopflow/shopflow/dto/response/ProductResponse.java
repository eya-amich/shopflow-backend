package com.shopflow.shopflow.dto.response;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String nom;
    private String description;
    private BigDecimal prix;
    private BigDecimal prixPromo;
    private Integer pourcentageRemise; // calculé
    private Integer stock;
    private boolean actif;
    private LocalDateTime dateCreation;
    private Integer totalVentes;

    // Vendeur
    private Long sellerId;
    private String sellerNomBoutique;

    // Relations
    private List<CategoryResponse> categories;
    private List<String> images;
    private List<ProductVariantResponse> variants;

    // Avis
    private Double noteMoyenne;
    private Integer nombreAvis;
}
