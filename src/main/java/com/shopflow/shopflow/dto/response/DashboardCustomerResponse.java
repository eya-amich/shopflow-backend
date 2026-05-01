package com.shopflow.shopflow.dto.response;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DashboardCustomerResponse {

    private Long totalCommandes;
    private List<OrderResponse> commandesEnCours;
    private List<ReviewResponse> derniersAvis;
}
