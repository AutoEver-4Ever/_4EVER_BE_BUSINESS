package org.ever._4ever_be_business.fcm.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FcmPeriodStatisticsDto {
    @JsonProperty("total_purchases")
    private FcmStatisticsValueDto totalPurchases;

    @JsonProperty("net_profit")
    private FcmStatisticsValueDto netProfit;

    @JsonProperty("accounts_receivable")
    private FcmStatisticsValueDto accountsReceivable;

    @JsonProperty("total_sales")
    private FcmStatisticsValueDto totalSales;
}
