package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;

import java.time.LocalDateTime;

@Entity
@Table(name="employee")
@NoArgsConstructor
@Getter
public class Employee extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="internel_user_id")
    private InternelUser internelUser;

    @Column(name="remaining_vacation")
    private Long remainingVacation;

    @Column(name="last_training_date")
    private LocalDateTime lastTrainingDate;

    public Employee(InternelUser internelUser, Long remainingVacation, LocalDateTime lastTrainingDate) {
        this.internelUser = internelUser;
        this.remainingVacation = remainingVacation;
        this.lastTrainingDate = lastTrainingDate;
    }
}
