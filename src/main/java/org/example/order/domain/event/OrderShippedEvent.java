package org.example.order.domain.event;

import org.example.order.domain.statemachine.OrderEventEnum;

public class OrderShippedEvent implements DomainEvent {
    private Long orderId;

    public OrderShippedEvent(Long orderId) {
        this.orderId = orderId;
    }


    @Override
    public String getEventId() {
        return String.format(OrderEventEnum.DELIVERY.name(), orderId);
    }
}