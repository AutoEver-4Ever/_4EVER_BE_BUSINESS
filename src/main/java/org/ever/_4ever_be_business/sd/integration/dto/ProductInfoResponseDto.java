package org.ever._4ever_be_business.sd.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoResponseDto {
    private List<ProductDto> products;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDto {
        @JsonProperty("product_id")
        private String productId;

        @JsonProperty("product_code")
        private String productCode;

        @JsonProperty("product_name")
        private String productName;
    }
}
