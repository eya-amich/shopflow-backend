package com.shopflow.shopflow.dto.request;


import com.shopflow. shopflow.enums.CouponType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CouponRequest {

    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotNull(message = "Le type est obligatoire")
    private CouponType type;

    @NotNull(message = "La valeur est obligatoire")
    @DecimalMin(value = "0.01", message = "La valeur doit être supérieure à 0")
    private BigDecimal valeur;

    private LocalDateTime dateExpiration;

    private Integer usagesMax;
}
