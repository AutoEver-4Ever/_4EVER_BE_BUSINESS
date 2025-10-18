package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;

@Entity
@Table(name="payroll_deducation")
@NoArgsConstructor
@Getter
public class PayrollDeducation extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="payroll_id")
    private Payroll payroll;

    @ManyToOne
    @JoinColumn(name="deducation_id")
    private Deducation deduction;

    public PayrollDeducation(Payroll payroll, Deducation deduction) {
        this.payroll = payroll;
        this.deduction = deduction;
    }
}
