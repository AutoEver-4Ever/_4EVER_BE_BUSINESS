package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.company.entity.CustomerCompany;

@Entity
@Table
@NoArgsConstructor
@Getter
public class CustomerUser extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public CustomerUser(Long userId, Long customerId, String customerName, CustomerCompany customerCompany, String customerUserCode) {
        this.userId = userId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerCompany = customerCompany;
        this.customerUserCode = customerUserCode;
    }
}
