package org.ever._4ever_be_business.voucher.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.order.entity.Order;
import org.ever._4ever_be_business.voucher.enums.PurchaseVoucherStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="sales_voucher")
@Getter
@NoArgsConstructor
public class SalesVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_company_id")
    private CustomerCompany customerCompany;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false, name = "voucher_code")
    private String voucherCode;

    @Column(nullable = false, name = "issue_date")
    private LocalDateTime issueDate;

    @Column(nullable = false, name = "due_date")
    private LocalDateTime dueDate;

    @Column(nullable = false, name = "total_amount")
    private BigDecimal totalAmount;

    @Column(nullable = false, name = "status")
    private PurchaseVoucherStatus status;

    @Column(nullable = false, length = 255)
    private String memo;
}
