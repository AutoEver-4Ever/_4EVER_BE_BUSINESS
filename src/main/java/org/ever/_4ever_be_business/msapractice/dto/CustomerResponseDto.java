package org.ever._4ever_be_business.msapractice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDto {
    private Long id;
    private String customerUserCode;
    private String companyName;
    private String businessNumber;
    private String ceoName;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private CustomerManagerDto manager;
    private String note;
    private String status;
}