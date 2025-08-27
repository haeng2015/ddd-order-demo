package org.example.order.interfaces.web;

import jakarta.validation.Valid;
import org.example.order.application.dto.OrderCreateCommand;
import org.example.order.application.service.OrderApplicationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderApplicationService orderService;

    public OrderController(OrderApplicationService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Long createOrder(@Valid @RequestBody OrderCreateCommand command) {
        return orderService.createOrder(command);
    }

    @PostMapping("/{id}/pay")
    public void payOrder(@PathVariable Long id) {
        orderService.payOrder(id);
    }

    @PostMapping("/{id}/ship")
    public void shipOrder(@PathVariable Long id) {
        orderService.shipOrder(id);
    }

    @PostMapping("/{id}/cancel")
    public void cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
    }
}