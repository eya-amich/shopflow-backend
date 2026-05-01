package com.shopflow.shopflow.service.impl;
import com.shopflow.shopflow. dto.request.CartItemRequest;
import com.shopflow.shopflow. dto.response.CartItemResponse;
import com.shopflow.shopflow. dto.response.CartResponse;
import com.shopflow.shopflow. entity.*;
import com.shopflow.shopflow. exception.BadRequestException;
import com.shopflow.shopflow. exception.InsufficientStockException;
import com.shopflow.shopflow. exception.ResourceNotFoundException;
import com.shopflow.shopflow. mapper.CartMapper;
import com.shopflow.shopflow. repository.*;
import com.shopflow.shopflow. service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final CartMapper cartMapper;

    private static final BigDecimal FRAIS_LIVRAISON = new BigDecimal("7.00");

    @Override
    public CartResponse getCart(String customerEmail) {
        Cart cart = getOrCreateCart(customerEmail);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItem(CartItemRequest request, String customerEmail) {
        Cart cart = getOrCreateCart(customerEmail);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        if (!product.isActif()) {
            throw new BadRequestException("Ce produit n'est plus disponible");
        }

        ProductVariant variant = null;
        if (request.getVariantId() != null) {
            variant = productVariantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variante non trouvée"));
        }

        // Vérifier le stock
        int stockDispo = product.getStock();
        if (variant != null && variant.getStockSupplementaire() != null) {
            stockDispo += variant.getStockSupplementaire();
        }
        if (request.getQuantite() > stockDispo) {
            throw new InsufficientStockException("Stock insuffisant. Disponible : " + stockDispo);
        }

        // Vérifier si l'article existe déjà dans le panier
        Optional<CartItem> existingItem;
        if (variant != null) {
            existingItem = cartItemRepository.findByCartIdAndProductIdAndVariantId(
                    cart.getId(), product.getId(), variant.getId());
        } else {
            existingItem = cartItemRepository.findByCartIdAndProductIdAndVariantIsNull(
                    cart.getId(), product.getId());
        }

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQty = item.getQuantite() + request.getQuantite();
            if (newQty > stockDispo) {
                throw new InsufficientStockException("Stock insuffisant pour cette quantité");
            }
            item.setQuantite(newQty);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .variant(variant)
                    .quantite(request.getQuantite())
                    .build();
            cart.getLignes().add(newItem);
        }

        cart = cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(Long itemId, Integer quantite, String customerEmail) {
        Cart cart = getOrCreateCart(customerEmail);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé dans le panier"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cet article n'appartient pas à votre panier");
        }

        if (quantite <= 0) {
            cart.getLignes().remove(item);
        } else {
            int stockDispo = item.getProduct().getStock();
            if (item.getVariant() != null && item.getVariant().getStockSupplementaire() != null) {
                stockDispo += item.getVariant().getStockSupplementaire();
            }
            if (quantite > stockDispo) {
                throw new InsufficientStockException("Stock insuffisant. Disponible : " + stockDispo);
            }
            item.setQuantite(quantite);
        }

        cart = cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long itemId, String customerEmail) {
        Cart cart = getOrCreateCart(customerEmail);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé dans le panier"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cet article n'appartient pas à votre panier");
        }

        cart.getLignes().remove(item);
        cart = cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse applyCoupon(String couponCode, String customerEmail) {
        Cart cart = getOrCreateCart(customerEmail);

        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new ResourceNotFoundException("Code promo non trouvé"));

        if (!coupon.isActif()) {
            throw new BadRequestException("Ce code promo n'est plus actif");
        }
        if (coupon.getDateExpiration() != null && coupon.getDateExpiration().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Ce code promo est expiré");
        }
        if (coupon.getUsagesMax() != null && coupon.getUsagesActuels() >= coupon.getUsagesMax()) {
            throw new BadRequestException("Ce code promo a atteint son nombre maximum d'utilisations");
        }

        cart.setCoupon(coupon);
        cart = cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeCoupon(String customerEmail) {
        Cart cart = getOrCreateCart(customerEmail);
        cart.setCoupon(null);
        cart = cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    // ===== Méthodes utilitaires =====

    private Cart getOrCreateCart(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        return cartRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().customer(customer).build();
                    return cartRepository.save(newCart);
                });
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItemResponse> lignes = cartMapper.toCartItemResponseList(cart.getLignes());

        BigDecimal sousTotal = lignes.stream()
                .map(CartItemResponse::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remise = BigDecimal.ZERO;
        String couponCode = null;

        if (cart.getCoupon() != null) {
            couponCode = cart.getCoupon().getCode();
            Coupon coupon = cart.getCoupon();
            switch (coupon.getType()) {
                case PERCENT:
                    remise = sousTotal.multiply(coupon.getValeur())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    break;
                case FIXED:
                    remise = coupon.getValeur();
                    break;
            }
            if (remise.compareTo(sousTotal) > 0) {
                remise = sousTotal;
            }
        }

        BigDecimal frais = cart.getLignes().isEmpty() ? BigDecimal.ZERO : FRAIS_LIVRAISON;
        BigDecimal totalTTC = sousTotal.subtract(remise).add(frais);

        return CartResponse.builder()
                .id(cart.getId())
                .lignes(lignes)
                .sousTotal(sousTotal)
                .fraisLivraison(frais)
                .remise(remise)
                .totalTTC(totalTTC)
                .couponCode(couponCode)
                .dateModification(cart.getDateModification())
                .build();
    }
}
