package org.ever._4ever_be_business.fcm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ARInvoiceSearchConditionDto {

    private String company;
    private LocalDate startDate;
    private LocalDate endDate;
}
