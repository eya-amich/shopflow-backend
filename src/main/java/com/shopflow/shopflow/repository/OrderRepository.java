package com.shopflow.shopflow.repository;

import com.shopflow.shopflow.entity.Order;
import com.shopflow.shopflow.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    Optional<Order> findByNumeroCommande(String numeroCommande);

    List<Order> findByStatut(OrderStatus statut);

    // Commandes contenant des produits d'un vendeur
    @Query("SELECT DISTINCT o FROM Order o JOIN o.lignes li WHERE li.product.seller.id = :sellerId")
    Page<Order> findBySellerProducts(@Param("sellerId") Long sellerId, Pageable pageable);

    // Commandes en attente d'un vendeur
    @Query("SELECT DISTINCT o FROM Order o JOIN o.lignes li " +
            "WHERE li.product.seller.id = :sellerId AND o.statut = :statut")
    List<Order> findBySellerAndStatut(@Param("sellerId") Long sellerId, @Param("statut") OrderStatus statut);

    // Chiffre d'affaires global
    @Query("SELECT COALESCE(SUM(o.totalTTC), 0) FROM Order o WHERE o.statut NOT IN ('CANCELLED')")
    BigDecimal calculateTotalRevenue();

    // Chiffre d'affaires d'un vendeur
    @Query("SELECT COALESCE(SUM(li.prixUnitaire * li.quantite), 0) FROM OrderItem li " +
            "WHERE li.product.seller.id = :sellerId AND li.order.statut NOT IN ('CANCELLED')")
    BigDecimal calculateSellerRevenue(@Param("sellerId") Long sellerId);

    // Compter les commandes récentes
    long countByStatut(OrderStatus statut);

    // Vérifier si un client a acheté un produit (pour les avis)
    @Query("SELECT COUNT(li) > 0 FROM OrderItem li " +
            "WHERE li.order.customer.id = :customerId AND li.product.id = :productId " +
            "AND li.order.statut = 'DELIVERED'")
    boolean hasCustomerPurchasedProduct(@Param("customerId") Long customerId, @Param("productId") Long productId);
}

