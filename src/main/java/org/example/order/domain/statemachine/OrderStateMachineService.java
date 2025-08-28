// application/service/OrderStateMachineService.java
package org.example.order.domain.statemachine;

import org.example.order.domain.model.OrderStatusEnum;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

/**
 * 订单状态机服务
 */
@Service
public class OrderStateMachineService {

    private final StateMachine<OrderStatusEnum, OrderEventEnum> orderStateMachine;

    public OrderStateMachineService(StateMachine<OrderStatusEnum, OrderEventEnum> orderStateMachine) {
        this.orderStateMachine = orderStateMachine;
    }

    public String getCurrentState() {
        return orderStateMachine.getState().getId().name();
    }

    public boolean isFinalState() {
        return orderStateMachine.getState().getId() == OrderStatusEnum.FINISH;
    }

}