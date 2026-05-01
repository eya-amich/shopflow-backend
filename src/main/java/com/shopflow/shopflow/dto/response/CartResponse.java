package com.shopflow.shopflow.dto.response;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CartResponse {

    private Long id;
    private List<CartItemResponse> lignes;
    private BigDecimal sousTotal;
    private BigDecimal fraisLivraison;
    private BigDecimal remise;
    private BigDecimal totalTTC;
    private String couponCode;
    private LocalDateTime dateModification;
}
