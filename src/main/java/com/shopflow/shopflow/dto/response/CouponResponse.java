package com.shopflow.shopflow.dto.response;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CouponResponse {

    private Long id;
    private String code;
    private String type;
    private BigDecimal valeur;
    private LocalDateTime dateExpiration;
    private Integer usagesMax;
    private Integer usagesActuels;
    private boolean actif;
    private boolean valide; // calculé : actif + non expiré + usages restants
}
