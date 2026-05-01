package com.shopflow.shopflow.service.impl;

import com.shopflow.shopflow.dto.request.ProductRequest;
import com.shopflow.shopflow.dto.request.ProductVariantRequest;
import com.shopflow.shopflow.dto.response.PageResponse;
import com.shopflow.shopflow.dto.response.ProductResponse;
import com.shopflow.shopflow.entity.Category;
import com.shopflow.shopflow.entity.Product;
import com.shopflow.shopflow.entity.ProductVariant;
import com.shopflow.shopflow.entity.User;
import com.shopflow.shopflow.enums.Role;
import com.shopflow.shopflow.exception.ResourceNotFoundException;
import com.shopflow.shopflow.exception.UnauthorizedException;
import com.shopflow.shopflow.mapper.ProductMapper;
import com.shopflow.shopflow.repository.CategoryRepository;
import com.shopflow.shopflow.repository.ProductRepository;
import com.shopflow.shopflow.repository.ReviewRepository;
import com.shopflow.shopflow.repository.UserRepository;
import com.shopflow.shopflow.service.ProductService;
import com.shopflow.shopflow.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request, String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Vendeur non trouvé"));

        if (seller.getRole() != Role.SELLER && seller.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Seuls les vendeurs peuvent créer des produits");
        }

        Product product = Product.builder()
                .seller(seller)
                .nom(request.getNom())
                .description(request.getDescription())
                .prix(request.getPrix())
                .prixPromo(request.getPrixPromo())
                .stock(request.getStock())
                .actif(true)
                .totalVentes(0)
                .images(request.getImages() != null ? request.getImages() : new ArrayList<>())
                .build();

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            product.setCategories(categories);
        }

        if (request.getVariants() != null) {
            for (ProductVariantRequest variantReq : request.getVariants()) {
                ProductVariant variant = ProductVariant.builder()
                        .product(product)
                        .attribut(variantReq.getAttribut())
                        .valeur(variantReq.getValeur())
                        .stockSupplementaire(variantReq.getStockSupplementaire())
                        .prixDelta(variantReq.getPrixDelta())
                        .build();
                product.getVariants().add(variant);
            }
        }

        product = productRepository.save(product);
        return enrichProductResponse(productMapper.toResponse(product), product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request, String sellerEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id : " + id));

        User user = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (user.getRole() != Role.ADMIN && !product.getSeller().getId().equals(user.getId())) {
            throw new UnauthorizedException("Vous ne pouvez modifier que vos propres produits");
        }

        product.setNom(request.getNom());
        product.setDescription(request.getDescription());
        product.setPrix(request.getPrix());
        product.setPrixPromo(request.getPrixPromo());
        product.setStock(request.getStock());

        if (request.getImages() != null) {
            product.getImages().clear();
            product.getImages().addAll(request.getImages());
        }

        if (request.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            product.setCategories(categories);
        }

        if (request.getVariants() != null) {
            product.getVariants().clear();
            for (ProductVariantRequest variantReq : request.getVariants()) {
                ProductVariant variant = ProductVariant.builder()
                        .product(product)
                        .attribut(variantReq.getAttribut())
                        .valeur(variantReq.getValeur())
                        .stockSupplementaire(variantReq.getStockSupplementaire())
                        .prixDelta(variantReq.getPrixDelta())
                        .build();
                product.getVariants().add(variant);
            }
        }

        product = productRepository.save(product);
        return enrichProductResponse(productMapper.toResponse(product), product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id, String userEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (user.getRole() != Role.ADMIN && !product.getSeller().getId().equals(user.getId())) {
            throw new UnauthorizedException("Vous ne pouvez supprimer que vos propres produits");
        }

        // Hard delete — suppression définitive
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public ProductResponse activateProduct(Long id, String userEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (user.getRole() != Role.ADMIN && !product.getSeller().getId().equals(user.getId())) {
            throw new UnauthorizedException("Accès refusé");
        }

        product.setActif(true);
        product = productRepository.save(product);
        return enrichProductResponse(productMapper.toResponse(product), product);
    }

    @Override
    @Transactional
    public ProductResponse deactivateProduct(Long id, String userEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (user.getRole() != Role.ADMIN && !product.getSeller().getId().equals(user.getId())) {
            throw new UnauthorizedException("Accès refusé");
        }

        product.setActif(false);
        product = productRepository.save(product);
        return enrichProductResponse(productMapper.toResponse(product), product);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id : " + id));
        return enrichProductResponse(productMapper.toResponse(product), product);
    }

    @Override
    public PageResponse<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> page = productRepository.findAll(pageable);
        return toPageResponse(page);
    }

    @Override
    public PageResponse<ProductResponse> searchProducts(String query, Pageable pageable) {
        Page<Product> page = productRepository.searchByQuery(query, pageable);
        return toPageResponse(page);
    }

    @Override
    public PageResponse<ProductResponse> filterProducts(Long categoryId, Long sellerId,
                                                        BigDecimal prixMin, BigDecimal prixMax,
                                                        Boolean promo, String query, Pageable pageable) {
        Specification<Product> spec = Specification.where(ProductSpecification.isActif());

        if (categoryId != null) spec = spec.and(ProductSpecification.hasCategoryId(categoryId));
        if (sellerId != null) spec = spec.and(ProductSpecification.hasSellerId(sellerId));
        if (prixMin != null) spec = spec.and(ProductSpecification.prixMin(prixMin));
        if (prixMax != null) spec = spec.and(ProductSpecification.prixMax(prixMax));
        if (promo != null && promo) spec = spec.and(ProductSpecification.isPromo());
        if (query != null && !query.isBlank()) {
            spec = spec.and(ProductSpecification.searchByNomOrDescription(query));
        }

        Page<Product> page = productRepository.findAll(spec, pageable);
        return toPageResponse(page);
    }

    @Override
    public List<ProductResponse> getTopSellingProducts() {
        return productRepository.findTop10ByActifTrueOrderByTotalVentesDesc().stream()
                .map(product -> enrichProductResponse(productMapper.toResponse(product), product))
                .collect(Collectors.toList());
    }

    // ===== Méthodes utilitaires =====

    private ProductResponse enrichProductResponse(ProductResponse response, Product product) {
        try {
            Double avgRating = reviewRepository.calculateAverageRating(product.getId());
            long reviewCount = reviewRepository.countByProductIdAndApprouveTrue(product.getId());
            response.setNoteMoyenne(avgRating != null ? avgRating : 0.0);
            response.setNombreAvis((int) reviewCount);
        } catch (Exception e) {
            response.setNoteMoyenne(0.0);
            response.setNombreAvis(0);
        }
        return response;
    }

    private PageResponse<ProductResponse> toPageResponse(Page<Product> page) {
        List<ProductResponse> content = page.getContent().stream()
                .map(product -> enrichProductResponse(productMapper.toResponse(product), product))
                .collect(Collectors.toList());

        return PageResponse.<ProductResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}