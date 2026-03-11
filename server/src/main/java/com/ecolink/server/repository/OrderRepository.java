package com.ecolink.server.repository;

import com.ecolink.server.domain.Order;
import com.ecolink.server.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Order> findByIdAndUserId(Long id, Long userId);
    List<Order> findByStatusAndPaidAtBefore(OrderStatus status, LocalDateTime paidAt);
    List<Order> findByStatusAndShippedAtBefore(OrderStatus status, LocalDateTime shippedAt);
    Page<Order> findByOrderNoContaining(String orderNo, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
