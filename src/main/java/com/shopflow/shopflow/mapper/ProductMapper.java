package com.shopflow.shopflow.mapper;

import com.shopflow. shopflow.dto.response.ProductResponse;
import com.shopflow. shopflow.dto.response.ProductVariantResponse;
import com.shopflow.shopflow.entity.Product;
import com.shopflow.shopflow.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {

    @Mapping(target = "sellerId", source = "seller.id")
    @Mapping(target = "sellerNomBoutique", expression = "java(product.getSeller().getSellerProfile() != null ? product.getSeller().getSellerProfile().getNomBoutique() : null)")
    @Mapping(target = "pourcentageRemise", expression = "java(calculatePourcentageRemise(product))")
    @Mapping(target = "noteMoyenne", ignore = true)
    @Mapping(target = "nombreAvis", ignore = true)
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);

    ProductVariantResponse variantToResponse(ProductVariant variant);

    List<ProductVariantResponse> variantsToResponseList(List<ProductVariant> variants);

    default Integer calculatePourcentageRemise(Product product) {
        if (product.getPrixPromo() != null && product.getPrix() != null
                && product.getPrix().doubleValue() > 0) {
            double remise = (1 - product.getPrixPromo().doubleValue() / product.getPrix().doubleValue()) * 100;
            return (int) Math.round(remise);
        }
        return null;
    }
}
