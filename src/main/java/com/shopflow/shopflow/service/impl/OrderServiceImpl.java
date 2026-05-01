package com.shopflow.shopflow.service.impl;
import com.shopflow.shopflow. dto.request.OrderRequest;
import com.shopflow.shopflow. dto.response.OrderResponse;
import com.shopflow.shopflow. dto.response.PageResponse;
import com.shopflow.shopflow. entity.*;
import com.shopflow.shopflow. enums.OrderStatus;
import com.shopflow.shopflow.enums.Role;
import com.shopflow.shopflow. exception.BadRequestException;
import com.shopflow.shopflow. exception.InsufficientStockException;
import com.shopflow.shopflow. exception.ResourceNotFoundException;
import com.shopflow.shopflow. exception.UnauthorizedException;
import com.shopflow.shopflow. mapper.OrderMapper;
import com.shopflow.shopflow. repository.*;
import com.shopflow.shopflow. service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final OrderMapper orderMapper;

    private static final BigDecimal FRAIS_LIVRAISON = new BigDecimal("7.00");

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request, String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new BadRequestException("Panier non trouvé"));

        if (cart.getLignes().isEmpty()) {
            throw new BadRequestException("Le panier est vide");
        }

        // Récupérer l'adresse
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Adresse non trouvée"));

        if (!address.getUser().getId().equals(customer.getId())) {
            throw new BadRequestException("Cette adresse ne vous appartient pas");
        }

        String adresseComplete = address.getRue() + ", " + address.getCodePostal() + " "
                + address.getVille() + ", " + address.getPays();

        // Vérifier le stock et calculer le sous-total
        BigDecimal sousTotal = BigDecimal.ZERO;

        Order order = Order.builder()
                .customer(customer)
                .statut(OrderStatus.PENDING)
                .numeroCommande(generateOrderNumber())
                .adresseLivraison(adresseComplete)
                .isNew(true)
                .build();

        for (CartItem cartItem : cart.getLignes()) {
            Product product = cartItem.getProduct();

            // Vérification stock finale
            int stockDispo = product.getStock();
            if (cartItem.getVariant() != null && cartItem.getVariant().getStockSupplementaire() != null) {
                stockDispo += cartItem.getVariant().getStockSupplementaire();
            }
            if (cartItem.getQuantite() > stockDispo) {
                throw new InsufficientStockException("Stock insuffisant pour : " + product.getNom());
            }

            // Calculer le prix unitaire
            BigDecimal prixUnitaire = product.getPrixPromo() != null ? product.getPrixPromo() : product.getPrix();
            if (cartItem.getVariant() != null && cartItem.getVariant().getPrixDelta() != null) {
                prixUnitaire = prixUnitaire.add(cartItem.getVariant().getPrixDelta());
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .variant(cartItem.getVariant())
                    .quantite(cartItem.getQuantite())
                    .prixUnitaire(prixUnitaire)
                    .build();

            order.getLignes().add(orderItem);
            sousTotal = sousTotal.add(prixUnitaire.multiply(BigDecimal.valueOf(cartItem.getQuantite())));

            // Déduire le stock
            product.setStock(product.getStock() - cartItem.getQuantite());
            product.setTotalVentes(product.getTotalVentes() + cartItem.getQuantite());
            productRepository.save(product);
        }

        // Appliquer le coupon s'il existe
        BigDecimal remise = BigDecimal.ZERO;
        if (cart.getCoupon() != null) {
            Coupon coupon = cart.getCoupon();
            switch (coupon.getType()) {
                case PERCENT:
                    remise = sousTotal.multiply(coupon.getValeur())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    break;
                case FIXED:
                    remise = coupon.getValeur();
                    break;
            }
            if (remise.compareTo(sousTotal) > 0) {
                remise = sousTotal;
            }
            // Incrémenter l'usage du coupon
            coupon.setUsagesActuels(coupon.getUsagesActuels() + 1);
            couponRepository.save(coupon);
        }

        order.setSousTotal(sousTotal);
        order.setFraisLivraison(FRAIS_LIVRAISON);
        order.setTotalTTC(sousTotal.subtract(remise).add(FRAIS_LIVRAISON));

        order = orderRepository.save(order);

        // Vider le panier
        cart.getLignes().clear();
        cart.setCoupon(null);
        cartRepository.save(cart);

        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'id : " + id));
        return orderMapper.toResponse(order);
    }

    @Override
    public PageResponse<OrderResponse> getMyOrders(String customerEmail, Pageable pageable) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Page<Order> page = orderRepository.findByCustomerId(customer.getId(), pageable);
        return toPageResponse(page);
    }

    @Override
    public PageResponse<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return toPageResponse(page);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus newStatus, String userEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        order.setStatut(newStatus);
        order.setNew(true);
        order = orderRepository.save(order);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long id, String customerEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new UnauthorizedException("Cette commande ne vous appartient pas");
        }

        if (order.getStatut() != OrderStatus.PENDING && order.getStatut() != OrderStatus.PAID) {
            throw new BadRequestException("Impossible d'annuler une commande avec le statut : " + order.getStatut());
        }

        order.setStatut(OrderStatus.CANCELLED);

        // Remettre le stock
        for (OrderItem item : order.getLignes()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantite());
            product.setTotalVentes(product.getTotalVentes() - item.getQuantite());
            productRepository.save(product);
        }

        order = orderRepository.save(order);
        return orderMapper.toResponse(order);
    }

    // ===== Méthodes utilitaires =====

    private String generateOrderNumber() {
        long count = orderRepository.count() + 1;
        return String.format("ORD-%d-%05d", Year.now().getValue(), count);
    }

    private PageResponse<OrderResponse> toPageResponse(Page<Order> page) {
        List<OrderResponse> content = page.getContent().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<OrderResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
