package org.ever._4ever_be_business.fcm.service;

import org.ever._4ever_be_business.fcm.dto.response.APInvoiceDetailDto;

public interface APInvoiceService {
    /**
     * AP 전표 상세 정보 조회
     *
     * @param invoiceId 전표 ID
     * @return APInvoiceDetailDto
     */
    APInvoiceDetailDto getAPInvoiceDetail(String invoiceId);
}
