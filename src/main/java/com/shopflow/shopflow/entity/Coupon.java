package com.shopflow.shopflow.entity;
import com.shopflow.shopflow.enums.CouponType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valeur;

    private LocalDateTime dateExpiration;

    private Integer usagesMax;

    @Builder.Default
    private Integer usagesActuels = 0;

    @Builder.Default
    private boolean actif = true;
}
