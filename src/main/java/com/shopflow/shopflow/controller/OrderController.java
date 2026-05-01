package com.shopflow.shopflow.controller;

import com.shopflow.shopflow.dto.request.OrderRequest;
import com.shopflow.shopflow.dto.request.UpdateOrderStatusRequest;
import com.shopflow.shopflow.dto.response.OrderResponse;
import com.shopflow.shopflow.dto.response.PageResponse;
import com.shopflow.shopflow.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication) {
        return new ResponseEntity<>(orderService.createOrder(request, authentication.getName()), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponse<OrderResponse>> getMyOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCommande").descending());
        return ResponseEntity.ok(orderService.getMyOrders(authentication.getName(), pageable));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCommande").descending());
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request.getStatut(), authentication.getName()));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(orderService.cancelOrder(id, authentication.getName()));
    }
}