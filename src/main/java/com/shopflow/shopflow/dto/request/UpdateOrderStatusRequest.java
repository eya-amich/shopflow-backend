package com.shopflow.shopflow.dto.request;


import com.shopflow. shopflow.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {

    @NotNull(message = "Le statut est obligatoire")
    private OrderStatus statut;
}

