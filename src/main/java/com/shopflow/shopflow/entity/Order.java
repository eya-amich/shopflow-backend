package com.shopflow.shopflow.entity;



import com.shopflow.shopflow.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus statut;

    @Column(nullable = false, unique = true)
    private String numeroCommande;

    @Column(nullable = false, length = 500)
    private String adresseLivraison;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal sousTotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fraisLivraison;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalTTC;

    @CreationTimestamp
    private LocalDateTime dateCommande;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> lignes = new ArrayList<>();

    @Builder.Default
    private boolean isNew = true;
}
