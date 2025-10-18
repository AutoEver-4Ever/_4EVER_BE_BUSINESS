package org.ever._4ever_be_business.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name="order_code")
    private String orderCode;

    @OneToOne
    @JoinColumn(name="quotation_id")
    private Quotation quotation;

    @OneToOne(mappedBy = "order")
    private OrderShipment OrderShipment;

    @Column(nullable = false, name="customer_user_id")
    private Long customerUserId;

    @Column(nullable = false, name="total_price")
    private BigDecimal totalPrice;

    @Column(nullable = false, name="order_date")
    private LocalDateTime orderDate;

    @Column(nullable = false, name="due_date")
    private LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name="status_id")
    private OrderStatus status;

    public Order(String orderCode, Quotation quotation, OrderShipment orderShipment, Long customerUserId, BigDecimal totalPrice, LocalDateTime orderDate, LocalDateTime dueDate, OrderStatus status) {
        this.orderCode = orderCode;
        this.quotation = quotation;
        OrderShipment = orderShipment;
        this.customerUserId = customerUserId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.dueDate = dueDate;
        this.status = status;
    }
}
