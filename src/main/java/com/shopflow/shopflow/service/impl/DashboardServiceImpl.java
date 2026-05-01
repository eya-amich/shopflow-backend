package com.shopflow.shopflow.service.impl;
import com.shopflow.shopflow.dto.response.*;
import com.shopflow.shopflow. entity.Order;
import com.shopflow.shopflow. entity.User;
import com.shopflow.shopflow. enums.OrderStatus;
import com.shopflow.shopflow. exception.ResourceNotFoundException;
import com.shopflow.shopflow. mapper.OrderMapper;
import com.shopflow.shopflow. mapper.ProductMapper;
import com.shopflow.shopflow. mapper.ReviewMapper;
import com.shopflow.shopflow. mapper.UserMapper;
import com.shopflow.shopflow. repository.*;
import com.shopflow.shopflow. service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final ReviewMapper reviewMapper;

    @Override
    public DashboardAdminResponse getAdminDashboard() {
        Pageable top5 = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dateCommande"));

        return DashboardAdminResponse.builder()
                .chiffreAffairesGlobal(orderRepository.calculateTotalRevenue())
                .totalCommandes(orderRepository.count())
                .totalUtilisateurs(userRepository.count())
                .totalProduits(productRepository.count())
                .commandesEnAttente(orderRepository.countByStatut(OrderStatus.PENDING))
                .topProduits(
                        productRepository.findTop10ByActifTrueOrderByTotalVentesDesc().stream()
                                .map(productMapper::toResponse)
                                .collect(Collectors.toList())
                )
                .commandesRecentes(
                        orderRepository.findAll(top5).getContent().stream()
                                .map(orderMapper::toResponse)
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Override
    public DashboardSellerResponse getSellerDashboard(String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Vendeur non trouvé"));

        Pageable top5 = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dateCommande"));
        List<Order> pendingOrders = orderRepository.findBySellerAndStatut(seller.getId(), OrderStatus.PENDING);

        return DashboardSellerResponse.builder()
                .revenus(orderRepository.calculateSellerRevenue(seller.getId()))
                .totalCommandes(orderRepository.findBySellerProducts(seller.getId(), Pageable.unpaged()).getTotalElements())
                .commandesEnAttente((long) pendingOrders.size())
                .totalProduits(productRepository.countBySellerId(seller.getId()))
                .alertesStockFaible(
                        productRepository.findLowStockBySeller(seller.getId(), 5).stream()
                                .map(productMapper::toResponse)
                                .collect(Collectors.toList())
                )
                .commandesRecentes(
                        orderRepository.findBySellerProducts(seller.getId(), top5).getContent().stream()
                                .map(orderMapper::toResponse)
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Override
    public DashboardCustomerResponse getCustomerDashboard(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        Pageable recent = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dateCommande"));

        return DashboardCustomerResponse.builder()
                .totalCommandes(orderRepository.findByCustomerId(customer.getId(), Pageable.unpaged()).getTotalElements())
                .commandesEnCours(
                        orderRepository.findByCustomerId(customer.getId(), recent).getContent().stream()
                                .filter(o -> o.getStatut() != OrderStatus.DELIVERED && o.getStatut() != OrderStatus.CANCELLED)
                                .map(orderMapper::toResponse)
                                .collect(Collectors.toList())
                )
                .derniersAvis(
                        reviewRepository.findByCustomerId(customer.getId()).stream()
                                .map(reviewMapper::toResponse)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
