package org.ever._4ever_be_business.fcm.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderInfoResponseDto {
    private List<ProductOrderItemDto> items;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductOrderItemDto {
        @JsonProperty("itemId")
        private String itemId;

        @JsonProperty("itemName")
        private String itemName;

        private Long quantity;

        @JsonProperty("uomName")
        private String uomName;

        @JsonProperty("unitPrice")
        private BigDecimal unitPrice;

        @JsonProperty("totalPrice")
        private BigDecimal totalPrice;
    }
}
