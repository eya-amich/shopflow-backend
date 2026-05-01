package com.shopflow.shopflow.mapper;
import com.shopflow.shopflow.dto.response.CartItemResponse;
import com.shopflow.shopflow.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productNom", source = "product.nom")
    @Mapping(target = "productImage", expression = "java(cartItem.getProduct().getImages() != null && !cartItem.getProduct().getImages().isEmpty() ? cartItem.getProduct().getImages().get(0) : null)")
    @Mapping(target = "prixUnitaire", expression = "java(calculatePrixUnitaire(cartItem))")
    @Mapping(target = "variantId", expression = "java(cartItem.getVariant() != null ? cartItem.getVariant().getId() : null)")
    @Mapping(target = "variantAttribut", expression = "java(cartItem.getVariant() != null ? cartItem.getVariant().getAttribut() : null)")
    @Mapping(target = "variantValeur", expression = "java(cartItem.getVariant() != null ? cartItem.getVariant().getValeur() : null)")
    @Mapping(target = "total", expression = "java(calculateTotal(cartItem))")
    @Mapping(target = "stockDisponible", expression = "java(cartItem.getProduct().getStock())")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItems);

    default BigDecimal calculatePrixUnitaire(CartItem cartItem) {
        BigDecimal prix = cartItem.getProduct().getPrixPromo() != null
                ? cartItem.getProduct().getPrixPromo()
                : cartItem.getProduct().getPrix();
        if (cartItem.getVariant() != null && cartItem.getVariant().getPrixDelta() != null) {
            prix = prix.add(cartItem.getVariant().getPrixDelta());
        }
        return prix;
    }

    default BigDecimal calculateTotal(CartItem cartItem) {
        return calculatePrixUnitaire(cartItem).multiply(BigDecimal.valueOf(cartItem.getQuantite()));
    }
}
