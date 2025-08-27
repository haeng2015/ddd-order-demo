package org.example.order.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.order.domain.event.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.random.RandomGenerator;

/**
 * 订单实体 - 聚合根
 * 表示客户的一个订单，包含多个订单项
 */
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA需要无参构造函数，但限制为protected以强制通过工厂方法创建
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNum;

    private Long customerId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductItem> productItems = new ArrayList<>();


    /**
     * 创建新订单
     *
     * @param customerId 客户ID
     * @param items      订单项列表
     * @return 订单实例
     */
    public static Order create(Long customerId, List<ProductItem> items) {
        if (customerId == null) {
            throw new IllegalArgumentException("客户ID不能为空");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("订单项不能为空");
        }

        long id = RandomGenerator.getDefault().nextLong();
        Order order = new Order();
        order.id = id;
        order.orderNum = generateOrderNumber();
        order.customerId = customerId;
        order.status = OrderStatus.CREATED;
        order.createdAt = LocalDateTime.now();
        order.updatedAt = order.createdAt;

        // 添加订单项并计算总金额
        BigDecimal total = BigDecimal.ZERO;
        for (ProductItem item : items) {
            order.addOrderItem(item);
            total = total.add(item.getSubtotal());
        }
        order.totalAmount = total;
        registerEvent(new OrderCreatedEvent(id));

        return order;
    }

    /**
     * 添加订单项
     *
     * @param item 订单项
     */
    public void addOrderItem(ProductItem item) {
        productItems.add(item);
        item.setOrder(this);
        recalculateTotalAmount();
    }

    /**
     * 移除订单项
     *
     * @param item 订单项
     */
    public void removeOrderItem(ProductItem item) {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("只有在创建状态的订单才能移除订单项");
        }
        productItems.remove(item);
        recalculateTotalAmount();
    }

    /**
     * 重新计算订单总金额
     */
    private void recalculateTotalAmount() {
        this.totalAmount = productItems.stream()
                .map(ProductItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 支付订单
     */
    public void pay() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("只有创建状态的订单才能支付");
        }
        this.status = OrderStatus.PAID;
        this.updatedAt = LocalDateTime.now();
        registerEvent(new OrderPaidEvent(id, new Money(totalAmount, "CNY")));
    }

    /**
     * 确认订单
     */
    public void confirm() {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("只有已支付的订单才能确认");
        }
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 发货
     */
    public void ship() {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("只有已确认的订单才能发货");
        }
        this.status = OrderStatus.SHIPPED;
        this.updatedAt = LocalDateTime.now();

        registerEvent(new OrderShippedEvent(id));
    }

    /**
     * 完成订单
     */
    public void complete() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("只有已发货的订单才能完成");
        }
        this.status = OrderStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 取消订单
     */
    public void cancel() {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("已发货或已完成的订单不能取消");
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();

        registerEvent(new OrderCancelledEvent(id, customerId));
    }

    /**
     * 退款
     */
    public void refund() {
        if (status != OrderStatus.PAID && status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("只有已支付或已确认的订单才能退款");
        }
        this.status = OrderStatus.REFUNDED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 获取订单项列表（不可修改）
     *
     * @return 订单项列表
     */
    public List<ProductItem> getProductItems() {
        return Collections.unmodifiableList(productItems);
    }

    /**
     * 生成订单编号
     *
     * @return 订单编号
     */
    private static String generateOrderNumber() {
        // 使用时间戳和UUID生成唯一订单号
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
    }


    @Transient
    private static final List<DomainEvent> events = new ArrayList<>();

    protected static void registerEvent(DomainEvent event) {
        events.add(event);
    }

    public List<DomainEvent> getEvents() {
        return new ArrayList<>(events);
    }

    public void clearEvents() {
        events.clear();
    }
}
