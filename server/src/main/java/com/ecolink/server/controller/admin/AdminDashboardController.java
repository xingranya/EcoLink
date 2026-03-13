package com.ecolink.server.controller.admin;

import com.ecolink.server.common.ApiResponse;
import com.ecolink.server.domain.Order;
import com.ecolink.server.domain.Product;
import com.ecolink.server.domain.enums.OrderStatus;
import com.ecolink.server.domain.enums.ProductStatus;
import com.ecolink.server.repository.CategoryRepository;
import com.ecolink.server.repository.OrderRepository;
import com.ecolink.server.repository.ProductRepository;
import com.ecolink.server.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 后台仪表盘接口
 */
@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardController {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public AdminDashboardController(ProductRepository productRepository,
                                    OrderRepository orderRepository,
                                    UserRepository userRepository,
                                    CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> stats() {
        BigDecimal revenue = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() != OrderStatus.UNPAID)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> data = Map.ofEntries(
                Map.entry("productCount", productRepository.count()),
                Map.entry("orderCount", orderRepository.count()),
                Map.entry("userCount", userRepository.count()),
                Map.entry("categoryCount", categoryRepository.count()),
                Map.entry("onSaleProductCount", productRepository.countByStatus(ProductStatus.ON_SALE)),
                Map.entry("offSaleProductCount", productRepository.countByStatus(ProductStatus.OFF_SALE)),
                Map.entry("lowStockProductCount", productRepository.countByStockLessThanEqual(50)),
                Map.entry("unpaidOrderCount", orderRepository.countByStatus(OrderStatus.UNPAID)),
                Map.entry("paidOrderCount", orderRepository.countByStatus(OrderStatus.PAID)),
                Map.entry("shippedOrderCount", orderRepository.countByStatus(OrderStatus.SHIPPED)),
                Map.entry("completedOrderCount", orderRepository.countByStatus(OrderStatus.COMPLETED)),
                Map.entry("revenueAmount", revenue),
                Map.entry("recentOrders", orderRepository.findTop5ByOrderByCreatedAtDescIdDesc().stream().map(this::toOrderCard).toList()),
                Map.entry("hotProducts", productRepository.findTop5ByOrderBySalesDescIdDesc().stream().map(this::toProductCard).toList())
        );
        return ApiResponse.ok(data);
    }

    private Map<String, Object> toOrderCard(Order order) {
        return Map.of(
                "id", order.getId(),
                "orderNo", order.getOrderNo(),
                "status", order.getStatus().name(),
                "receiverName", order.getReceiverName(),
                "totalAmount", order.getTotalAmount(),
                "createdAt", order.getCreatedAt() != null ? order.getCreatedAt().toString() : ""
        );
    }

    private Map<String, Object> toProductCard(Product product) {
        return Map.of(
                "id", product.getId(),
                "name", product.getName(),
                "sales", product.getSales(),
                "stock", product.getStock(),
                "status", product.getStatus().name(),
                "mainImage", product.getMainImage() != null ? product.getMainImage() : ""
        );
    }
}
