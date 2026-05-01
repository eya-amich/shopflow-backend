package com.shopflow.shopflow.dto.response;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AddressResponse {

    private Long id;
    private String rue;
    private String ville;
    private String codePostal;
    private String pays;
    private boolean principal;
}
