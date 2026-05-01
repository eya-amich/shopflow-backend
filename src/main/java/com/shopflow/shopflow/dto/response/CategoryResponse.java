package com.shopflow.shopflow.dto.response;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CategoryResponse {

    private Long id;
    private String nom;
    private String description;
    private Long parentId;
    private String parentNom;
    private List<CategoryResponse> sousCategories;
}
