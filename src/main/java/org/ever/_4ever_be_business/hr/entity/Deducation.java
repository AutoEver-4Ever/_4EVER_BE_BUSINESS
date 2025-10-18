package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;

import java.math.BigDecimal;

@Entity
@Table(name="deducation")
@NoArgsConstructor
@Getter
public class Deducation extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="amount")
    private BigDecimal amount;

    public Deducation(String title, BigDecimal amount) {
        this.title = title;
        this.amount = amount;
    }
}
