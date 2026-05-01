package com.shopflow.shopflow.repository;



import com.shopflow.shopflow.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Page<Product> findByActifTrue(Pageable pageable);

    Page<Product> findBySellerId(Long sellerId, Pageable pageable);
    // Compter les avis approuvés pour un produit


    // Recherche full-text sur nom et description
    @Query("SELECT p FROM Product p WHERE p.actif = true AND " +
            "(LOWER(p.nom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> searchByQuery(@Param("query") String query, Pageable pageable);

    // Produits en promotion
    @Query("SELECT p FROM Product p WHERE p.actif = true AND p.prixPromo IS NOT NULL")
    Page<Product> findPromoProducts(Pageable pageable);

    // Top 10 meilleures ventes
    List<Product> findTop10ByActifTrueOrderByTotalVentesDesc();

    // Compter les produits d'un vendeur
    long countBySellerId(Long sellerId);

    // Produits avec stock faible
    @Query("SELECT p FROM Product p WHERE p.seller.id = :sellerId AND p.stock < :seuil AND p.actif = true")
    List<Product> findLowStockBySeller(@Param("sellerId") Long sellerId, @Param("seuil") int seuil);

    // Récupérer les produits d'un vendeur spécifique

    @Query("SELECT AVG(r.note) FROM Review r WHERE r.product.id = :productId AND r.approuve = true")
    Double calculateAverageRating(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.approuve = true")
    long countByProductIdAndApprouveTrue(@Param("productId") Long productId);



}
