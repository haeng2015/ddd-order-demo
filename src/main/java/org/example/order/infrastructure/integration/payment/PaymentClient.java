package org.example.order.infrastructure.integration.payment;

import org.example.order.infrastructure.integration.payment.dto.PaymentRequestDTO;
import org.example.order.infrastructure.integration.payment.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "payment-client",
        url = "${payment.service.url:http://localhost:8081}",
        fallbackFactory = PaymentClientFallbackFactory.class)
public interface PaymentClient {

    @PostMapping("/api/payments/process")
    PaymentResponse processPayment(@RequestBody PaymentRequestDTO request);

    @PostMapping("/api/payments/cancel")
    PaymentResponse cancelPayment(@RequestBody PaymentRequestDTO request);
}