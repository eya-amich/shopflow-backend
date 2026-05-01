package com.shopflow.shopflow.dto.response;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;
    private Long productId;
    private String productNom;
    private Long customerId;
    private String customerNom;
    private Integer note;
    private String commentaire;
    private LocalDateTime dateCreation;
    private boolean approuve;
}
