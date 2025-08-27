package org.example.order.domain.repository;

import org.example.order.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * <b>
 * <p>
 *
 * @Author He.hp
 * @Email haeng2015@163.com
 * @Date 2025/8/27 17:13
 */
@Component
public interface OrderRepository extends JpaRepository<Order, Long> {
}
