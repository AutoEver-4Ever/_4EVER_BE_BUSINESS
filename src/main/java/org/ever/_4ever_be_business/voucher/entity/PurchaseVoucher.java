package org.ever._4ever_be_business.voucher.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.voucher.enums.PurchaseVoucherStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="sales_voucher")
@NoArgsConstructor
@Getter
public class PurchaseVoucher extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // scm 연동
    @Column(nullable = false, name = "supplier_company_id")
    private Long supplierCompanyId;
    // scm 연동
    @Column(nullable = false, name = "product_order_id")
    private Long productOrder;

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
