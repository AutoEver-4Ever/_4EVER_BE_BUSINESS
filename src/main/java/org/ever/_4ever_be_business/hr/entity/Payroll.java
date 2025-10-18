package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.hr.enums.PayrollStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="payroll")
@NoArgsConstructor
@Getter
public class Payroll extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="employee_id")
    private Employee employee;

    @Column(name="base_salary")
    private BigDecimal baseSalary;

    @Column(name="overtime_salary")
    private BigDecimal overtimeSalary;

    @Column(name="status")
    private PayrollStatus status;

    @Column(name="net_salary")
    private BigDecimal netSalary;

    @Column(name="pay_date")
    private LocalDateTime payDate;

    @Column(name="base_date")
    private LocalDateTime baseDate;

    public Payroll(Employee employee, BigDecimal baseSalary, BigDecimal overtimeSalary, PayrollStatus status, BigDecimal netSalary, LocalDateTime payDate, LocalDateTime baseDate) {
        this.employee = employee;
        this.baseSalary = baseSalary;
        this.overtimeSalary = overtimeSalary;
        this.status = status;
        this.netSalary = netSalary;
        this.payDate = payDate;
        this.baseDate = baseDate;
    }
}
