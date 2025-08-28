package org.example.order.domain.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.example.order.domain.model.Order;
import org.example.order.domain.model.OrderStatusEnum;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.Objects;

/**
 * <b> 通过代码方式发送状态转换事件
 * <p>
 *
 * @Author He.hp
 * @Email haeng2015@163.com
 * @Date 2025/8/28 11:42
 */
@Slf4j
public abstract class OrderStateMachineManager {
    private final StateMachine<OrderStatusEnum, OrderEventEnum> orderStateMachine;
    private final StateMachinePersister<OrderStatusEnum, OrderEventEnum, Order> persister;

    protected OrderStateMachineManager(StateMachine<OrderStatusEnum, OrderEventEnum> orderStateMachine, StateMachinePersister<OrderStatusEnum, OrderEventEnum, Order> persister) {
        this.orderStateMachine = orderStateMachine;
        this.persister = persister;
    }

    /**
     * 发送状态转换事件
     *
     * @return
     */
    protected synchronized boolean sendEvent(OrderEventEnum event, Order order) {
        Message<OrderEventEnum> message = MessageBuilder.withPayload(event).
                setHeader("order", order).build();

        return sendEvent(message);
    }

    /**
     * 发送状态转换事件
     *
     * @param message
     * @return
     */
    protected synchronized boolean sendEvent(Message<OrderEventEnum> message) {
        log.info("当前ID为: {}", message.getHeaders().getId());
        boolean result = false;
        Order order = (Order) message.getHeaders().get("order");
        try {
//            orderStateMachine.start();
            orderStateMachine.startReactively().subscribe();

            //尝试恢复状态机状态
            persister.restore(orderStateMachine, order);
            result = orderStateMachine.sendEvent(message);

            //持久化状态机状态
            persister.persist(orderStateMachine, order);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (Objects.nonNull(order) &&
                    Objects.equals(order.getStatus(), OrderStatusEnum.FINISH)
                    ) {
                orderStateMachine.stopReactively().subscribe();
            }
        }
        return result;
    }

}
