package org.ever._4ever_be_business.fcm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.dto.response.ARInvoiceListItemDto;
import org.ever._4ever_be_business.fcm.dto.response.SupplierPurchaseInvoiceListItemDto;
import org.ever._4ever_be_business.fcm.service.ARInvoiceService;
import org.ever._4ever_be_business.fcm.service.CustomerDashboardInvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerDashboardInvoiceServiceImpl implements CustomerDashboardInvoiceService {

    private final ARInvoiceService arInvoiceService;

    @Override
    public List<SupplierPurchaseInvoiceListItemDto> getCustomerInvoices(String userId, int size) {
        int limit = size > 0 ? size : 5;

        Page<ARInvoiceListItemDto> page = arInvoiceService.getARInvoiceList(
                userId, // company
                null,
                null,
                0,
                limit
        );

        return page.stream()
                .map(this::toDashboardItem)
                .toList();
    }

    private SupplierPurchaseInvoiceListItemDto toDashboardItem(ARInvoiceListItemDto src) {
        String companyName = src.getSupply() != null ? src.getSupply().getSupplierName() : "고객사 미지정";
        return SupplierPurchaseInvoiceListItemDto.builder()
                .itemId(src.getInvoiceId())
                .itemNumber(src.getInvoiceNumber())
                .itemTitle(companyName)
                .name(companyName)
                .statusCode(src.getStatusCode())
                .date(src.getIssueDate())
                .build();
    }
}
