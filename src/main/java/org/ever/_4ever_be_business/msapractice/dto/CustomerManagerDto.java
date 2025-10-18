package org.ever._4ever_be_business.msapractice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerManagerDto {
    @NotBlank(message = "담당자명은 필수 입력값입니다")
    private String name;

    @Pattern(regexp = "010-\\d{4}-\\d{4}", message = "휴대폰 번호 형식이 올바르지 않습니다")
    private String mobile;

    private String email;
}