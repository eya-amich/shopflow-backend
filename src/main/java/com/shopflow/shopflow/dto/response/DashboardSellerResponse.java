package com.shopflow.shopflow.dto.response;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DashboardSellerResponse {

    private BigDecimal revenus;
    private Long totalCommandes;
    private Long commandesEnAttente;
    private Long totalProduits;
    private List<ProductResponse> alertesStockFaible;
    private List<OrderResponse> commandesRecentes;
}