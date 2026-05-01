package com.shopflow.shopflow.service;


import com.shopflow.shopflow.dto.request.CartItemRequest;
import com.shopflow. shopflow.dto.response.CartResponse;

public interface CartService {
    CartResponse getCart(String customerEmail);
    CartResponse addItem(CartItemRequest request, String customerEmail);
    CartResponse updateItemQuantity(Long itemId, Integer quantite, String customerEmail);
    CartResponse removeItem(Long itemId, String customerEmail);
    CartResponse applyCoupon(String couponCode, String customerEmail);
    CartResponse removeCoupon(String customerEmail);
}
