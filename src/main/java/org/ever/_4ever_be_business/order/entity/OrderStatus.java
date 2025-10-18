package org.ever._4ever_be_business.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;

@Entity
@Table(name = "order_status")
@NoArgsConstructor
@Getter
public class OrderStatus extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    public OrderStatus(String status) {
        this.status = status;
    }
}
