package org.ever._4ever_be_business.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;

@Entity
@Table(name = "order_status")
@NoArgsConstructor
@Getter
public class OrderStatus extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    private String status;

    public OrderStatus(String status) {
        this.status = status;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
