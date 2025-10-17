package org.ever._4ever_be_business.msapractice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequestDto {
    @NotBlank(message = "회사명은 필수 입력값입니다")
    private String companyName;

    @NotBlank(message = "사업자 번호는 필수 입력값입니다")
    @Pattern(regexp = "\\d{3}-\\d{2}-\\d{5}", message = "사업자 번호 형식이 올바르지 않습니다")
    private String businessNumber;

    @NotBlank(message = "대표자명은 필수 입력값입니다")
    private String ceoName;

    @Pattern(regexp = "\\d{2,3}-\\d{3,4}-\\d{4}", message = "연락처 형식이 올바르지 않습니다")
    private String contactPhone;

    private String contactEmail;
    private String address;

    @Valid
    private CustomerManagerDto manager;

    private String note;
}