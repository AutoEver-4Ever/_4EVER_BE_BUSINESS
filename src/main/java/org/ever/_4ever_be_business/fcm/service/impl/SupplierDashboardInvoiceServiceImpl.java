package org.ever._4ever_be_business.fcm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.dto.response.PurchaseInvoiceListDto;
import org.ever._4ever_be_business.fcm.dto.response.SupplierPurchaseInvoiceListItemDto;
import org.ever._4ever_be_business.fcm.service.PurchaseStatementService;
import org.ever._4ever_be_business.fcm.service.SupplierDashboardInvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierDashboardInvoiceServiceImpl implements SupplierDashboardInvoiceService {

    private final PurchaseStatementService purchaseStatementService;

    @Override
    public List<SupplierPurchaseInvoiceListItemDto> getSupplierInvoices(String supplierUserId, int size) {
        int limit = size > 0 ? size : 5;

        Page<PurchaseInvoiceListDto> page = purchaseStatementService.getPurchaseStatementListBySupplierUserId(
                supplierUserId,
                null,
                null,
                PageRequest.of(0, limit)
        );

        return page.map(this::toDashboardItem).getContent();
    }

    private SupplierPurchaseInvoiceListItemDto toDashboardItem(PurchaseInvoiceListDto src) {
        String title = src.getConnection() != null ? src.getConnection().getConnectionName() : "";
        return SupplierPurchaseInvoiceListItemDto.builder()
                .itemId(src.getInvoiceId())
                .itemNumber(src.getInvoiceCode())
                .itemTitle(title)
                .name(title)
                .statusCode(src.getStatus())
                .date(src.getIssueDate())
                .build();
    }
}
