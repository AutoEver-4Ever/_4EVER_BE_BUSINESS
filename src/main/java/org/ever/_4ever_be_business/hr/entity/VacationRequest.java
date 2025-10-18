package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.hr.enums.VacationType;

import java.time.LocalDateTime;

@Entity
@Table(name="vacation_request")
@NoArgsConstructor
@Getter
public class VacationRequest extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="employee_id")
    private Employee employee;

    @OneToOne
    @JoinColumn(name="vacation_request_approval_id")
    private VacationRequestApproval vacationRequestApprovalId;

    @Column(name="vacation_type")
    private VacationType vacationType;

    @Column(name="requested_start_date")
    private LocalDateTime requestedStartDate;

    @Column(name="requested_end_date")
    private LocalDateTime requestedEndDate;

    public VacationRequest(Employee employee, VacationRequestApproval vacationRequestApprovalId, VacationType vacationType, LocalDateTime requestedStartDate, LocalDateTime requestedEndDate) {
        this.employee = employee;
        this.vacationRequestApprovalId = vacationRequestApprovalId;
        this.vacationType = vacationType;
        this.requestedStartDate = requestedStartDate;
        this.requestedEndDate = requestedEndDate;
    }
}
