package com.shopflow.shopflow.controller;
import com.shopflow.shopflow. dto.request.CartItemRequest;
import com.shopflow.shopflow. dto.response.CartResponse;
import com.shopflow.shopflow. service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getCart(authentication.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @Valid @RequestBody CartItemRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(cartService.addItem(request, authentication.getName()));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @PathVariable Long itemId,
            @RequestParam Integer quantite,
            Authentication authentication) {
        return ResponseEntity.ok(cartService.updateItemQuantity(itemId, quantite, authentication.getName()));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable Long itemId,
            Authentication authentication) {
        return ResponseEntity.ok(cartService.removeItem(itemId, authentication.getName()));
    }

    @PostMapping("/coupon")
    public ResponseEntity<CartResponse> applyCoupon(
            @RequestParam String code,
            Authentication authentication) {
        return ResponseEntity.ok(cartService.applyCoupon(code, authentication.getName()));
    }

    @DeleteMapping("/coupon")
    public ResponseEntity<CartResponse> removeCoupon(Authentication authentication) {
        return ResponseEntity.ok(cartService.removeCoupon(authentication.getName()));
    }
}