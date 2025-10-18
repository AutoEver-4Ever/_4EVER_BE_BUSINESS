package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;

import java.math.BigDecimal;

@Entity
@Table(name="position")
@NoArgsConstructor
@Getter
public class Position extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String positionCode;

    private String positionName;

    @ManyToOne
    @JoinColumn(name="department_id")
    private Department department;

    @Column(name="is_manager")
    private Boolean isManager;

    @Column(name="salary")
    private BigDecimal salary;
}
