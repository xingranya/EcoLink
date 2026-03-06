package com.ecolink.server.service;

import com.ecolink.server.domain.*;
import com.ecolink.server.domain.enums.OrderStatus;
import com.ecolink.server.dto.order.CreateOrderRequest;
import com.ecolink.server.dto.order.OrderItemResponse;
import com.ecolink.server.dto.order.OrderResponse;
import com.ecolink.server.exception.BizException;
import com.ecolink.server.repository.OrderItemRepository;
import com.ecolink.server.repository.OrderRepository;
import com.ecolink.server.repository.OrderStatusLogRepository;
import com.ecolink.server.security.SecurityUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusLogRepository orderStatusLogRepository;
    private final AuthService authService;
    private final AddressService addressService;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, OrderStatusLogRepository orderStatusLogRepository, AuthService authService, AddressService addressService, CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderStatusLogRepository = orderStatusLogRepository;
        this.authService = authService;
        this.addressService = addressService;
        this.cartService = cartService;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Address address = addressService.findByIdForCurrentUser(request.addressId());
        List<CartItem> cartItems = cartService.findItemsForCurrentUser(request.cartItemIds());
        if (cartItems.isEmpty()) {
            throw new BizException(4006, "购物车为空");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            if (ci.getQuantity() > ci.getProduct().getStock()) {
                throw new BizException(4005, "商品库存不足: " + ci.getProduct().getName());
            }
            total = total.add(ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }

        Order order = new Order();
        order.setUser(authService.getCurrentUserEntity());
        order.setOrderNo(generateOrderNo());
        order.setStatus(OrderStatus.UNPAID);
        order.setTotalAmount(total);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getDetail());
        orderRepository.save(order);

        for (CartItem ci : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(ci.getProduct());
            item.setProductName(ci.getProduct().getName());
            item.setProductImage(ci.getProduct().getMainImage());
            item.setSalePrice(ci.getProduct().getPrice());
            item.setQuantity(ci.getQuantity());
            orderItemRepository.save(item);

            ci.getProduct().setStock(ci.getProduct().getStock() - ci.getQuantity());
            ci.getProduct().setSales(ci.getProduct().getSales() + ci.getQuantity());
        }

        writeStatusLog(order, null, OrderStatus.UNPAID, "订单创建");
        cartService.removeItems(cartItems);
        return toResponse(order, orderItemRepository.findByOrderIdOrderByIdAsc(order.getId()));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> list() {
        Long userId = SecurityUtils.currentUserId();
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream().map(order -> toResponse(order, orderItemRepository.findByOrderIdOrderByIdAsc(order.getId()))).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse detail(Long id) {
        Long userId = SecurityUtils.currentUserId();
        Order order = orderRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new BizException(4044, "订单不存在"));
        List<OrderItem> items = orderItemRepository.findByOrderIdOrderByIdAsc(order.getId());
        return toResponse(order, items);
    }

    @Transactional
    public OrderResponse pay(Long id) {
        Long userId = SecurityUtils.currentUserId();
        Order order = orderRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new BizException(4044, "订单不存在"));
        if (order.getStatus() != OrderStatus.UNPAID) {
            throw new BizException(4007, "订单状态不允许支付");
        }
        OrderStatus from = order.getStatus();
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);
        writeStatusLog(order, from, OrderStatus.PAID, "模拟支付成功");
        return toResponse(order, orderItemRepository.findByOrderIdOrderByIdAsc(order.getId()));
    }

    @Transactional
    @Scheduled(fixedDelay = 5000)
    public void autoFlow() {
        LocalDateTime now = LocalDateTime.now();
        List<Order> paidOrders = orderRepository.findByStatusAndPaidAtBefore(OrderStatus.PAID, now.minusSeconds(5));
        for (Order order : paidOrders) {
            OrderStatus from = order.getStatus();
            order.setStatus(OrderStatus.SHIPPED);
            order.setShippedAt(now);
            orderRepository.save(order);
            writeStatusLog(order, from, OrderStatus.SHIPPED, "系统自动发货");
        }

        List<Order> shippedOrders = orderRepository.findByStatusAndShippedAtBefore(OrderStatus.SHIPPED, now.minusSeconds(5));
        for (Order order : shippedOrders) {
            OrderStatus from = order.getStatus();
            order.setStatus(OrderStatus.COMPLETED);
            order.setCompletedAt(now);
            orderRepository.save(order);
            writeStatusLog(order, from, OrderStatus.COMPLETED, "系统自动完成");
        }
    }

    private void writeStatusLog(Order order, OrderStatus from, OrderStatus to, String note) {
        OrderStatusLog log = new OrderStatusLog();
        log.setOrder(order);
        log.setFromStatus(from);
        log.setToStatus(to);
        log.setNote(note);
        orderStatusLogRepository.save(log);
    }

    private String generateOrderNo() {
        long random = ThreadLocalRandom.current().nextLong(1000, 9999);
        return "ECO" + System.currentTimeMillis() + random;
    }

    private OrderResponse toResponse(Order order, List<OrderItem> items) {
        List<OrderItemResponse> itemResponses = items.stream().map(item -> new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProductName(),
                item.getProductImage(),
                item.getSalePrice(),
                item.getQuantity(),
                item.getSalePrice().multiply(BigDecimal.valueOf(item.getQuantity()))
        )).toList();
        return new OrderResponse(
                order.getId(),
                order.getOrderNo(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getReceiverName(),
                order.getReceiverPhone(),
                order.getReceiverAddress(),
                order.getPaidAt(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}
