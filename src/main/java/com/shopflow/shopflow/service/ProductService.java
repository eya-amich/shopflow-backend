package com.shopflow.shopflow.service;

import com.shopflow.shopflow.dto.request.ProductRequest;
import com.shopflow.shopflow.dto.response.PageResponse;
import com.shopflow.shopflow.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request, String sellerEmail);

    ProductResponse updateProduct(Long id, ProductRequest request, String sellerEmail);

    void deleteProduct(Long id, String userEmail);

    ProductResponse activateProduct(Long id, String userEmail);

    ProductResponse deactivateProduct(Long id, String userEmail);

    ProductResponse getProductById(Long id);

    PageResponse<ProductResponse> getAllProducts(Pageable pageable);

    PageResponse<ProductResponse> searchProducts(String query, Pageable pageable);

    PageResponse<ProductResponse> filterProducts(Long categoryId, Long sellerId,
                                                 BigDecimal prixMin, BigDecimal prixMax,
                                                 Boolean promo, String query, Pageable pageable);

    List<ProductResponse> getTopSellingProducts();
}