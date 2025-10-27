package org.ever._4ever_be_business.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotation")
@NoArgsConstructor
@Getter
public class Quotation extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, name="quotation_code")
    private String quotationCode;

    @Column(nullable = false, name="customer_user_id", length = 36)
    private String customerUserId;  // Changed from Long to String for UUID

    @Column(nullable = false, name="total_price")
    private BigDecimal totalPrice;  // Changed from Long to BigDecimal

    @OneToOne
    @JoinColumn(name="quotation_approval_id")
    private QuotationApproval quotationApproval;

    @Column(nullable = false, name="due_date")
    private LocalDateTime dueDate;

    @Column(name="note", columnDefinition = "TEXT")
    private String note;

    public Quotation(String quotationCode, String customerUserId, BigDecimal totalPrice,
                    QuotationApproval quotationApproval, LocalDateTime dueDate, String note) {
        this.quotationCode = quotationCode;
        this.customerUserId = customerUserId;
        this.totalPrice = totalPrice;
        this.quotationApproval = quotationApproval;
        this.dueDate = dueDate;
        this.note = note;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
