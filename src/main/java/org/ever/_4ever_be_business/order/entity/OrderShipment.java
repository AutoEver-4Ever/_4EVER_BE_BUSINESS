package org.ever._4ever_be_business.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.order.enums.ShipmentStatus;

import java.time.LocalDateTime;

@Entity
@Table(name="order_shipment")
@NoArgsConstructor
@Getter
public class OrderShipment extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="order_id", nullable = false)
    private Order order;

    @Column(nullable = false, name="order_shipment_date")
    private LocalDateTime orderShipmentDate;

    @Column(nullable = false, name="status")
    private ShipmentStatus status;

    public OrderShipment(Order order, LocalDateTime orderShipmentDate, ShipmentStatus status) {
        this.order = order;
        this.orderShipmentDate = orderShipmentDate;
        this.status = status;
    }
}
