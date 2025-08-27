package org.example.order.domain.event;

import org.example.order.domain.model.OrderStatus;

public class OrderCancelledEvent implements DomainEvent {
    private Long orderId;
    private Long customerId;

    private OrderStatus status = OrderStatus.CANCELLED;

    public OrderCancelledEvent(Long orderId, Long customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }

    @Override
    public String getEventId() {
        return String.format(status.name(), orderId);
    }
}