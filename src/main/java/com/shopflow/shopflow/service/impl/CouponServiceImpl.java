package com.shopflow.shopflow.service.impl;
import com.shopflow.shopflow. dto.request.CouponRequest;
import com.shopflow.shopflow. dto.response.CouponResponse;
import com.shopflow.shopflow. entity.Coupon;
import com.shopflow.shopflow. exception.DuplicateResourceException;
import com.shopflow.shopflow. exception.ResourceNotFoundException;
import com.shopflow.shopflow. mapper.CouponMapper;
import com.shopflow.shopflow. repository.CouponRepository;
import com.shopflow.shopflow. service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Un coupon avec ce code existe déjà");
        }

        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .type(request.getType())
                .valeur(request.getValeur())
                .dateExpiration(request.getDateExpiration())
                .usagesMax(request.getUsagesMax())
                .usagesActuels(0)
                .actif(true)
                .build();

        coupon = couponRepository.save(coupon);
        return couponMapper.toResponse(coupon);
    }

    @Override
    @Transactional
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon non trouvé"));

        coupon.setCode(request.getCode().toUpperCase());
        coupon.setType(request.getType());
        coupon.setValeur(request.getValeur());
        coupon.setDateExpiration(request.getDateExpiration());
        coupon.setUsagesMax(request.getUsagesMax());

        coupon = couponRepository.save(coupon);
        return couponMapper.toResponse(coupon);
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon non trouvé"));
        coupon.setActif(false);
        couponRepository.save(coupon);
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        return couponMapper.toResponseList(couponRepository.findAll());
    }

    @Override
    public CouponResponse validateCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon non trouvé avec le code : " + code));
        return couponMapper.toResponse(coupon);
    }
}
