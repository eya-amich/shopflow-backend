package com.shopflow.shopflow.service;
import com.shopflow.shopflow.dto.request.CouponRequest;
import com.shopflow.shopflow. dto.response.CouponResponse;
import java.util.List;

public interface CouponService {
    CouponResponse createCoupon(CouponRequest request);
    CouponResponse updateCoupon(Long id, CouponRequest request);
    void deleteCoupon(Long id);
    List<CouponResponse> getAllCoupons();
    CouponResponse validateCoupon(String code);
}
