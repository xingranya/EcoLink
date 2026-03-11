package com.ecolink.server.controller.admin;

import com.ecolink.server.common.ApiResponse;
import com.ecolink.server.repository.CategoryRepository;
import com.ecolink.server.repository.OrderRepository;
import com.ecolink.server.repository.ProductRepository;
import com.ecolink.server.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        Map<String, Object> data = Map.of(
                "productCount", productRepository.count(),
                "orderCount", orderRepository.count(),
                "userCount", userRepository.count(),
                "categoryCount", categoryRepository.count()
        );
        return ApiResponse.ok(data);
    }
}
