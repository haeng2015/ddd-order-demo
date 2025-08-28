package org.example.order.domain.event;

import org.example.order.domain.statemachine.OrderEventEnum;

public class OrderCancelledEvent implements DomainEvent {
    private Long orderId;
    private Long customerId;

    public OrderCancelledEvent(Long orderId, Long customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }

    @Override
    public String getEventId() {
        return String.format(OrderEventEnum.CANCELLED.name(), orderId);
    }
}