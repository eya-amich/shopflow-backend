package com.shopflow.shopflow.dto.response;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private String numeroCommande;
    private String statut;
    private String adresseLivraison;
    private BigDecimal sousTotal;
    private BigDecimal fraisLivraison;
    private BigDecimal totalTTC;
    private LocalDateTime dateCommande;
    private boolean isNew;
    private List<OrderItemResponse> lignes;

    // Info client
    private Long customerId;
    private String customerNom;
    private String customerEmail;
}
