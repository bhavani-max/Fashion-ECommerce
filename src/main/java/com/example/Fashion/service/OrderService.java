package com.example.Fashion.service;

import com.example.Fashion.dto.OrderRequestDTO;
import com.example.Fashion.dto.OrderResponseDTO;
import com.example.Fashion.dto.CartItemDTO;
import com.example.Fashion.model.*;
import com.example.Fashion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles order placement and retrieval.
 * On placement: creates the order, clears cart, and auto-creates a shipment record.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ShipmentService shipmentService;

    /**
     * Places an order from the user's current cart.
     * Automatically creates a shipment record after saving the order.
     */
    @Transactional
    public OrderResponseDTO placeOrder(Long userId, OrderRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItemDTO> cartItems = cartService.getCartItems(userId);
        if (cartItems.isEmpty()) throw new RuntimeException("Cart is empty");

        BigDecimal total = cartItems.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .totalAmount(total)
                .status(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .build();

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getProductPrice())
                    .build();
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        Order saved = orderRepository.save(order);

        // Clear cart after order
        cartService.clearCart(userId);

        // Auto-create shipment record
        shipmentService.createShipment(saved.getId());

        return toDTO(saved);
    }

    /** Returns all orders for a given user */
    public List<OrderResponseDTO> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private OrderResponseDTO toDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .createdAt(order.getCreatedAt())
                .build();
    }
}