package org.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * <b>
 * <p>
 *
 * @Author He.hp
 * @Email haeng2015@163.com
 * @Date 2025/8/27 15:06
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "org.example.order.infrastructure.integration")
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}