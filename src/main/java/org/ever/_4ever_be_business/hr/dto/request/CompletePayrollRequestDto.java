package org.ever._4ever_be_business.hr.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompletePayrollRequestDto {
    @JsonProperty("payrollId")
    private String payrollId;
}
