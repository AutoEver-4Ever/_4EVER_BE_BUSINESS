package org.ever._4ever_be_business.fcm.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto {

    @JsonProperty("companyId")
    private String companyId;

    @JsonProperty("companyCode")
    private String companyCode;

    @JsonProperty("companyName")
    private String companyName;
}
