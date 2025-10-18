package org.ever._4ever_be_business.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "quotation")
@NoArgsConstructor
@Getter
public class Quotation extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name="quotation_code")
    private String quotationCode;

    @Column(nullable = false, name="customer_user_id")
    private Long customerUserId;

    @Column(nullable = false, name="total_price")
    private Long totalPrice;

    @OneToOne
    @JoinColumn(name="quotation_approval_id")
    private QuotationApproval quotationApproval;

    @Column(nullable = false, name="due_date")
    private LocalDateTime dueDate;
}
