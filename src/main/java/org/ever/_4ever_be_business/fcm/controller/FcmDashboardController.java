package org.ever._4ever_be_business.fcm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.fcm.dto.response.SupplierPurchaseInvoiceListItemDto;
import org.ever._4ever_be_business.fcm.service.SupplierDashboardInvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/fcm/dashboard")
@RequiredArgsConstructor
public class FcmDashboardController {

    private final SupplierDashboardInvoiceService supplierDashboardInvoiceService;

    @GetMapping("/invoice/ap/supplier")
    public ApiResponse<List<SupplierPurchaseInvoiceListItemDto>> getSupplierApInvoices(
            @RequestParam("userId") String userId,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        log.info("[DASHBOARD][FCM] 공급사 매입 전표 목록 조회 요청 - userId: {}, size: {}", userId, size);

        List<SupplierPurchaseInvoiceListItemDto> invoices =
                supplierDashboardInvoiceService.getSupplierInvoices(userId, size);

        return ApiResponse.success(
                invoices,
                "공급사 매입 전표 목록 조회 성공",
                HttpStatus.OK
        );
    }
}
