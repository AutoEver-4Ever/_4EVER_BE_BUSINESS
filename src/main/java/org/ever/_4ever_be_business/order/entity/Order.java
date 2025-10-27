package org.ever._4ever_be_business.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, name="order_code")
    private String orderCode;

    @OneToOne
    @JoinColumn(name="quotation_id")
    private Quotation quotation;

    @OneToOne(mappedBy = "order")
    private OrderShipment OrderShipment;

    @Column(nullable = false, name="customer_user_id", length = 36)
    private String customerUserId;

    @Column(nullable = false, name="total_price")
    private BigDecimal totalPrice;

    @Column(nullable = false, name="order_date")
    private LocalDateTime orderDate;

    @Column(nullable = false, name="due_date")
    private LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name="status_id")
    private OrderStatus status;

    public Order(String orderCode, Quotation quotation, OrderShipment orderShipment, String customerUserId, BigDecimal totalPrice, LocalDateTime orderDate, LocalDateTime dueDate, OrderStatus status) {
        this.orderCode = orderCode;
        this.quotation = quotation;
        OrderShipment = orderShipment;
        this.customerUserId = customerUserId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
