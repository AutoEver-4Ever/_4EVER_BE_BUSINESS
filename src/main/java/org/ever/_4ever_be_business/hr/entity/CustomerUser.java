package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.company.entity.CustomerCompany;

@Entity
@Table
@NoArgsConstructor
@Getter
public class CustomerUser extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name="user_id")
    private Long userId;

    @Column(name="customer_id")
    private Long customerId;

    @Column(name="customer_name")
    private String customerName;

    @ManyToOne
    @JoinColumn(name="customer_company_id")
    private CustomerCompany customerCompany;

    @Column(name="customer_user_code")
    private String customerUserCode;

    @Column(name="email", length = 100)
    private String email;

    @Column(name="phone_number", length = 20)
    private String phoneNumber;

    public CustomerUser(Long userId, Long customerId, String customerName, CustomerCompany customerCompany, String customerUserCode, String email, String phoneNumber) {
        this.userId = userId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerCompany = customerCompany;
        this.customerUserCode = customerUserCode;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * 담당자 정보 수정
     */
    public void updateManagerInfo(String customerName, String email, String phoneNumber) {
        this.customerName = customerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
