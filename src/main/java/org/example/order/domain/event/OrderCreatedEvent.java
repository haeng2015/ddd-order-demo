package org.example.order.domain.event;

import org.example.order.domain.model.OrderStatus;

public class OrderCreatedEvent implements DomainEvent {
    private static final long serialVersionUID = 1L;
    private Long orderId;
    private OrderStatus status = OrderStatus.CREATED;

    public OrderCreatedEvent(Long orderId) {
        this.orderId = orderId;
    }

    @Override
    public String getEventId() {
        return String.format(status.name(), orderId);
    }
}