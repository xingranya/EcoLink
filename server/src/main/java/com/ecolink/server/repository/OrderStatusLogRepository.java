package com.ecolink.server.repository;

import com.ecolink.server.domain.OrderStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusLogRepository extends JpaRepository<OrderStatusLog, Long> {}
