package org.example.order.domain.event;

import org.example.order.domain.model.Money;
import org.example.order.domain.model.OrderStatus;

import java.time.LocalDateTime;

public class OrderPaidEvent implements DomainEvent {
    private Long orderId;
    private Money amount;
    private LocalDateTime occurredAt;
    private OrderStatus status = OrderStatus.PAID;

    public OrderPaidEvent(Long orderId, Money amount) {
        this.orderId = orderId;
        this.amount = amount;
        this.occurredAt = LocalDateTime.now();
    }


    @Override
    public String getEventId() {
        return String.format(status.name(), orderId);
    }
}