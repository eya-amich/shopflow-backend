package com.shopflow.shopflow.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String attribut;

    @Column(nullable = false)
    private String valeur;

    private Integer stockSupplementaire;

    @Column(precision = 10, scale = 2)
    private BigDecimal prixDelta;
}
