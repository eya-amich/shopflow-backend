package com.shopflow.shopflow.controller;

import com.shopflow.shopflow.dto.request.ProductRequest;
import com.shopflow.shopflow.dto.response.PageResponse;
import com.shopflow.shopflow.dto.response.ProductResponse;
import com.shopflow.shopflow.entity.Product;
import com.shopflow.shopflow.entity.User;
import com.shopflow.shopflow.exception.ResourceNotFoundException;
import com.shopflow.shopflow.mapper.ProductMapper;
import com.shopflow.shopflow.repository.ProductRepository;
import com.shopflow.shopflow.repository.UserRepository;
import com.shopflow.shopflow.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) BigDecimal prixMin,
            @RequestParam(required = false) BigDecimal prixMax,
            @RequestParam(required = false) Boolean promo,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (categoryId != null || sellerId != null || prixMin != null
                || prixMax != null || promo != null || (q != null && !q.isBlank())) {
            return ResponseEntity.ok(productService.filterProducts(
                    categoryId, sellerId, prixMin, prixMax, promo, q, pageable));
        }

        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductResponse>> searchProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        return ResponseEntity.ok(productService.searchProducts(q, pageable));
    }

    @GetMapping("/top-selling")
    public ResponseEntity<List<ProductResponse>> getTopSelling() {
        return ResponseEntity.ok(productService.getTopSellingProducts());
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request,
            Authentication authentication) {
        return new ResponseEntity<>(productService.createProduct(request, authentication.getName()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(productService.updateProduct(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, Authentication authentication) {
        productService.deleteProduct(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ProductResponse> activateProduct(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(productService.activateProduct(id, authentication.getName()));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ProductResponse> deactivateProduct(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(productService.deactivateProduct(id, authentication.getName()));
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponse<ProductResponse>> getMyProducts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        Sort sort = Sort.by("dateCreation").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Récupérer l'utilisateur (vendeur) connecté
        User seller = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Récupérer les produits de ce vendeur
        Page<Product> products = productRepository.findBySellerId(seller.getId(), pageable);

        // Construire la réponse
        List<ProductResponse> content = products.getContent().stream()
                .map(product -> {
                    ProductResponse response = productMapper.toResponse(product);
                    // Enrichir avec les notes
                    try {
                        Double avgRating = productRepository.calculateAverageRating(product.getId());
                        long reviewCount = productRepository.countByProductIdAndApprouveTrue(product.getId());
                        response.setNoteMoyenne(avgRating != null ? avgRating : 0.0);
                        response.setNombreAvis((int) reviewCount);
                    } catch (Exception e) {
                        response.setNoteMoyenne(0.0);
                        response.setNombreAvis(0);
                    }
                    return response;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(PageResponse.<ProductResponse>builder()
                .content(content)
                .pageNumber(products.getNumber())
                .pageSize(products.getSize())
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .first(products.isFirst())
                .last(products.isLast())
                .build());
    }
}