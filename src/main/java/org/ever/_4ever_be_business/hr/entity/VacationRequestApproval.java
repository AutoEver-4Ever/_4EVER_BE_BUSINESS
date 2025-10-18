package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.order.enums.ApprovalStatus;

import java.time.LocalDateTime;

@Entity
@Table(name="vacation_request_approval")
@NoArgsConstructor
@Getter
public class VacationRequestApproval extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private ApprovalStatus approvalStatus;

    @ManyToOne
    @JoinColumn(name="approved_by")
    private Employee approvedBy;

    @Column(name="approved_at")
    private LocalDateTime approvedAt;

    @Column(name="rejected_reason")
    private String rejectedReason;

    public VacationRequestApproval(ApprovalStatus approvalStatus, Employee approvedBy, LocalDateTime approvedAt, String rejectedReason) {
        this.approvalStatus = approvalStatus;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.rejectedReason = rejectedReason;
    }
}
