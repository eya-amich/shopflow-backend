package com.shopflow.shopflow.specification;

import com.shopflow.shopflow.entity.Product;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> isActif() {
        return (root, query, cb) -> cb.isTrue(root.get("actif"));
    }

    public static Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, cb) -> {
            query.distinct(true);
            var categories = root.join("categories", JoinType.INNER);
            return cb.equal(categories.get("id"), categoryId);
        };
    }

    public static Specification<Product> hasSellerId(Long sellerId) {
        return (root, query, cb) ->
                cb.equal(root.get("seller").get("id"), sellerId);
    }

    public static Specification<Product> prixMin(BigDecimal min) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("prix"), min);
    }

    public static Specification<Product> prixMax(BigDecimal max) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("prix"), max);
    }

    public static Specification<Product> isPromo() {
        return (root, query, cb) ->
                cb.isNotNull(root.get("prixPromo"));
    }

    public static Specification<Product> searchByNomOrDescription(String query) {
        return (root, criteriaQuery, cb) -> {
            String pattern = "%" + query.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("nom")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }
}