package com.shopflow.shopflow.mapper;
import com.shopflow.shopflow.dto.response.OrderItemResponse;
import com.shopflow.shopflow.dto.response.OrderResponse;
import com.shopflow.shopflow.entity.Order;
import com.shopflow.shopflow.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "statut", expression = "java(order.getStatut().name())")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerNom", expression = "java(order.getCustomer().getPrenom() + \" \" + order.getCustomer().getNom())")
    @Mapping(target = "customerEmail", source = "customer.email")
    OrderResponse toResponse(Order order);

    List<OrderResponse> toResponseList(List<Order> orders);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productNom", source = "product.nom")
    @Mapping(target = "productImage", expression = "java(orderItem.getProduct().getImages() != null && !orderItem.getProduct().getImages().isEmpty() ? orderItem.getProduct().getImages().get(0) : null)")
    @Mapping(target = "variantAttribut", expression = "java(orderItem.getVariant() != null ? orderItem.getVariant().getAttribut() : null)")
    @Mapping(target = "variantValeur", expression = "java(orderItem.getVariant() != null ? orderItem.getVariant().getValeur() : null)")
    @Mapping(target = "total", expression = "java(orderItem.getPrixUnitaire().multiply(java.math.BigDecimal.valueOf(orderItem.getQuantite())))")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> orderItems);
}