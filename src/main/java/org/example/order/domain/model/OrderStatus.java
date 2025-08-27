package org.example.order.domain.model;

/**
 * 订单状态枚举
 * 定义了订单在其生命周期中可能的所有状态
 */
public enum OrderStatus {
    /**
     * 已创建 - 订单刚刚被创建，尚未支付
     */
    CREATED,

    /**
     * 已支付 - 订单已成功支付，等待库存确认
     */
    PAID,

    /**
     * 已确认 - 库存已确认，订单准备发货
     */
    CONFIRMED,

    /**
     * 已发货 - 订单已发出，等待客户收货
     */
    SHIPPED,

    /**
     * 已完成 - 订单已被客户确认收货，交易完成
     */
    COMPLETED,

    /**
     * 已取消 - 订单被取消（可能是客户取消或系统自动取消）
     */
    CANCELLED,

    /**
     * 已退款 - 订单已退款
     */
    REFUNDED
}
