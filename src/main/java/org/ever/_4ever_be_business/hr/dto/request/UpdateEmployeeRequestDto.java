package org.ever._4ever_be_business.hr.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.hr.enums.Gender;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeRequestDto {
    @JsonProperty("employeeName")
    private String employeeName;

    @JsonProperty("departmentId")
    private String departmentId;

    @JsonProperty("positionId")
    private String positionId;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("birthDate")
    private String birthDate;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;
}
