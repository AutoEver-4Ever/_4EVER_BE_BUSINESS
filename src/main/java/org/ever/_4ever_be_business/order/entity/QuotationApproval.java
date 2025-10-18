package org.ever._4ever_be_business.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.order.enums.ApprovalStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "quotation_approval")
@NoArgsConstructor
@Getter
public class QuotationApproval extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name="approval_status")
    private ApprovalStatus approvalStatus;

    @Column(nullable = false, name="approved_at")
    private LocalDateTime approvedAt;

    @Column(nullable = false, name="approved_by_user_id")
    private Long approvedBy;

    @Column(nullable = false, name="rejected_reason")
    private String rejectedReason;

    public QuotationApproval(ApprovalStatus approvalStatus, LocalDateTime approvedAt, Long approvedBy, String rejectedReason) {
        this.approvalStatus = approvalStatus;
        this.approvedAt = approvedAt;
        this.approvedBy = approvedBy;
        this.rejectedReason = rejectedReason;
    }
}
