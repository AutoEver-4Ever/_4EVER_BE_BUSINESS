package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;

@Entity
@Table(name="employee_training")
@NoArgsConstructor
@Getter
public class EmployeeTraining extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name="training_id")
    private Training training;

    @Column(name="completion_status")
    private Boolean completionStatus;

    public EmployeeTraining(Employee employee, Training training, Boolean completionStatus) {
        this.employee = employee;
        this.training = training;
        this.completionStatus = completionStatus;
    }
}
