package com.ecolink.server.controller.admin;

import com.ecolink.server.common.ApiResponse;
import com.ecolink.server.domain.Order;
import com.ecolink.server.domain.OrderItem;
import com.ecolink.server.domain.enums.OrderStatus;
import com.ecolink.server.exception.BizException;
import com.ecolink.server.repository.OrderItemRepository;
import com.ecolink.server.repository.OrderRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 后台订单管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public AdminOrderController(OrderRepository orderRepository,
                                OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status) {
        PageRequest pageReq = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Order> result;
        if (orderNo != null && !orderNo.isBlank() && status != null && !status.isBlank()) {
            result = orderRepository.findByOrderNoContainingAndStatus(orderNo, OrderStatus.valueOf(status), pageReq);
        } else if (orderNo != null && !orderNo.isBlank()) {
            result = orderRepository.findByOrderNoContaining(orderNo, pageReq);
        } else if (status != null && !status.isBlank()) {
            result = orderRepository.findByStatus(OrderStatus.valueOf(status), pageReq);
        } else {
            result = orderRepository.findAll(pageReq);
        }
        Map<String, Object> data = Map.of(
                "content", result.getContent().stream().map(this::toMap).toList(),
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages(),
                "number", result.getNumber()
        );
        return ApiResponse.ok(data);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BizException(4040, "订单不存在"));
        List<OrderItem> items = orderItemRepository.findByOrderIdOrderByIdAsc(order.getId());
        Map<String, Object> data = Map.of(
                "order", toMap(order),
                "items", items.stream().map(item -> Map.of(
                        "id", item.getId(),
                        "productName", item.getProductName(),
                        "productImage", item.getProductImage() != null ? item.getProductImage() : "",
                        "salePrice", item.getSalePrice(),
                        "quantity", item.getQuantity(),
                        "subtotal", item.getSalePrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                )).toList()
        );
        return ApiResponse.ok(data);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable long id,
                                          @Valid @RequestBody StatusReq req) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BizException(4040, "订单不存在"));
        OrderStatus newStatus = OrderStatus.valueOf(req.status());
        order.setStatus(newStatus);
        if (newStatus == OrderStatus.SHIPPED) order.setShippedAt(LocalDateTime.now());
        if (newStatus == OrderStatus.COMPLETED) order.setCompletedAt(LocalDateTime.now());
        orderRepository.save(order);
        return ApiResponse.ok(null);
    }

    private Map<String, Object> toMap(Order o) {
        return Map.ofEntries(
                Map.entry("id", o.getId()),
                Map.entry("orderNo", o.getOrderNo()),
                Map.entry("status", o.getStatus().name()),
                Map.entry("totalAmount", o.getTotalAmount()),
                Map.entry("receiverName", o.getReceiverName()),
                Map.entry("receiverPhone", o.getReceiverPhone()),
                Map.entry("receiverAddress", o.getReceiverAddress()),
                Map.entry("createdAt", o.getCreatedAt() != null ? o.getCreatedAt().toString() : ""),
                Map.entry("paidAt", o.getPaidAt() != null ? o.getPaidAt().toString() : ""),
                Map.entry("shippedAt", o.getShippedAt() != null ? o.getShippedAt().toString() : ""),
                Map.entry("completedAt", o.getCompletedAt() != null ? o.getCompletedAt().toString() : "")
        );
    }

    public record StatusReq(@NotBlank String status) {}
}
