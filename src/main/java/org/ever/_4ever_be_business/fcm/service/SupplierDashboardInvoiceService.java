package org.ever._4ever_be_business.fcm.service;

import org.ever._4ever_be_business.fcm.dto.response.SupplierPurchaseInvoiceListItemDto;

import java.util.List;

public interface SupplierDashboardInvoiceService {
    List<SupplierPurchaseInvoiceListItemDto> getSupplierInvoices(String supplierUserId, int size);
}
