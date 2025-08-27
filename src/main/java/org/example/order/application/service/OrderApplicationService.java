package org.example.order.application.service;

import org.example.order.application.dto.OrderCreateCommand;
import org.example.order.domain.model.Money;
import org.example.order.domain.model.Order;
import org.example.order.domain.model.ProductItem;
import org.example.order.domain.repository.OrderRepository;
import org.example.order.infrastructure.integration.inventory.acl.InventoryServiceAcl;
import org.example.order.infrastructure.messaging.KafkaEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final InventoryServiceAcl inventoryAcl;
    private final KafkaEventPublisher eventPublisher;

    public OrderApplicationService(OrderRepository orderRepository,
                                   InventoryServiceAcl inventoryAcl,
                                   KafkaEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.inventoryAcl = inventoryAcl;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Long createOrder(OrderCreateCommand command) {
        if (!inventoryAcl.reserveStock(command.getProductId(), command.getQuantity())) {
            throw new RuntimeException("库存不足或服务不可用");
        }

        Order order = Order.create(command.getCustomerId(), Arrays.asList(
                ProductItem.create(command.getProductId(), command.getProductName(), command.getQuantity(), new Money(new BigDecimal(command.getPrice()), "CNY")
                )));
        orderRepository.save(order);

        order.getEvents().forEach(eventPublisher::publish);
        order.clearEvents();

        return order.getId();
    }

    @Transactional
    public void payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.pay();
        orderRepository.save(order);
        order.getEvents().forEach(eventPublisher::publish);
        order.clearEvents();
    }

    @Transactional
    public void shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.ship();
        orderRepository.save(order);
        order.getEvents().forEach(eventPublisher::publish);
        order.clearEvents();
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.cancel();
        orderRepository.save(order);
        order.getEvents().forEach(eventPublisher::publish);
        order.clearEvents();
    }
}