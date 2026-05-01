package com.shopflow.shopflow.mapper;
import com.shopflow.shopflow.dto.response.CouponResponse;
import com.shopflow.shopflow.entity.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    @Mapping(target = "type", expression = "java(coupon.getType().name())")
    @Mapping(target = "valide", expression = "java(isCouponValide(coupon))")
    CouponResponse toResponse(Coupon coupon);

    List<CouponResponse> toResponseList(List<Coupon> coupons);

    default boolean isCouponValide(Coupon coupon) {
        if (!coupon.isActif()) return false;
        if (coupon.getDateExpiration() != null && coupon.getDateExpiration().isBefore(LocalDateTime.now())) return false;
        if (coupon.getUsagesMax() != null && coupon.getUsagesActuels() >= coupon.getUsagesMax()) return false;
        return true;
    }
}