package org.example.order.domain.event;

import org.example.order.domain.model.OrderStatus;

public class OrderShippedEvent implements DomainEvent {
    private Long orderId;
    private OrderStatus status = OrderStatus.SHIPPED;

    public OrderShippedEvent(Long orderId) {
        this.orderId = orderId;
    }


    @Override
    public String getEventId() {
        return String.format(status.name(), orderId);
    }
}