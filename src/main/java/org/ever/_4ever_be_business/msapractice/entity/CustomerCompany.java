package org.ever._4ever_be_business.msapractice.entity;

import org.ever._4ever_be_business.common.audit.Auditable;
import org.ever._4ever_be_business.common.audit.EntityAuditListener;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "customer_company")
@EntityListeners(EntityAuditListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerCompany extends TimeStamp implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_user_id", nullable = false, unique = true)
    private String customerUserId;  // UUID 값

    @Column(name = "company_code", length = 20)
    private String companyCode;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "business_number", nullable = false, length = 20, unique = true)
    private String businessNumber;

    @Column(name = "ceo_name", nullable = false, length = 50)
    private String ceoName;

    @Column(name = "zipcode", length = 10)
    private String zipcode;

    @Column(name = "base_address", length = 255)
    private String baseAddress;

    @Column(name = "detail_address", length = 255)
    private String detailAddress;

    @Column(name = "office_phone", length = 20)
    private String officePhone;

    @Column(name = "office_email", length = 100)
    private String officeEmail;

    @Column(name = "etc", length = 255)
    private String etc;
    
    @Override
    public String getAuditableId() {
        return id.toString();
    }

    @Override
    public String getAuditableType() {
        return "customerCompany";
    }
}