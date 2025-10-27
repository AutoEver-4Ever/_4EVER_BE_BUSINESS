package org.ever._4ever_be_business.fcm.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierCompanyResponseDto {
    @JsonProperty("companyId")
    private String companyId;

    @JsonProperty("companyCode")
    private String companyCode;

    @JsonProperty("companyName")
    private String companyName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("category")
    private String category;

    @JsonProperty("officePhone")
    private String officePhone;

    @JsonProperty("managerId")
    private String managerId;
}
