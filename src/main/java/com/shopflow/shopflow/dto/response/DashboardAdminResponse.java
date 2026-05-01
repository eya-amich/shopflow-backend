package com.shopflow.shopflow.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DashboardAdminResponse {

    private BigDecimal chiffreAffairesGlobal;
    private Long totalCommandes;
    private Long totalUtilisateurs;
    private Long totalProduits;
    private Long commandesEnAttente;
    private List<ProductResponse> topProduits;
    private List<UserResponse> topVendeurs;
    private List<OrderResponse> commandesRecentes;
}
