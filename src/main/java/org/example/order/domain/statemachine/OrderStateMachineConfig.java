package org.example.order.domain.statemachine;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachine
public class OrderStateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderState, OrderEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states
            .withStates()
            .initial(OrderState.CREATED)
            .states(EnumSet.allOf(OrderState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        transitions
            .withExternal()
                .source(OrderState.CREATED).target(OrderState.PAID).event(OrderEvent.PAY)
                .and()
            .withExternal()
                .source(OrderState.PAID).target(OrderState.SHIPPED).event(OrderEvent.SHIP)
                .and()
            .withExternal()
                .source(OrderState.CREATED).target(OrderState.CANCELLED).event(OrderEvent.CANCEL)
                .and()
            .withExternal()
                .source(OrderState.PAID).target(OrderState.CANCELLED).event(OrderEvent.CANCEL);
    }
}