package com.shopflow.shopflow.service;
import com.shopflow.shopflow.dto.request.OrderRequest;
import com.shopflow. shopflow.dto.response.OrderResponse;
import com.shopflow. shopflow.dto.response.PageResponse;
import com.shopflow. shopflow.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request, String customerEmail);
    OrderResponse getOrderById(Long id);
    PageResponse<OrderResponse> getMyOrders(String customerEmail, Pageable pageable);
    PageResponse<OrderResponse> getAllOrders(Pageable pageable);
    OrderResponse updateOrderStatus(Long id, OrderStatus newStatus, String userEmail);
    OrderResponse cancelOrder(Long id, String customerEmail);
}
