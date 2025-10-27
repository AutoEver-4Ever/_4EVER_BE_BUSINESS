package org.ever._4ever_be_business.fcm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.fcm.dto.response.APInvoiceDetailDto;
import org.ever._4ever_be_business.fcm.dto.response.APInvoiceItemDto;
import org.ever._4ever_be_business.fcm.integration.dto.ProductOrderInfoResponseDto;
import org.ever._4ever_be_business.fcm.integration.dto.SupplierCompanyResponseDto;
import org.ever._4ever_be_business.fcm.integration.port.ProductOrderServicePort;
import org.ever._4ever_be_business.fcm.integration.port.SupplierCompanyServicePort;
import org.ever._4ever_be_business.fcm.service.APInvoiceService;
import org.ever._4ever_be_business.voucher.entity.PurchaseVoucher;
import org.ever._4ever_be_business.voucher.repository.PurchaseVoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class APInvoiceServiceImpl implements APInvoiceService {

    private final PurchaseVoucherRepository purchaseVoucherRepository;
    private final SupplierCompanyServicePort supplierCompanyServicePort;
    private final ProductOrderServicePort productOrderServicePort;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional(readOnly = true)
    public APInvoiceDetailDto getAPInvoiceDetail(String invoiceId) {
        log.info("AP 전표 상세 정보 조회 - invoiceId: {}", invoiceId);

        // 1. PurchaseVoucher 조회
        PurchaseVoucher voucher = purchaseVoucherRepository.findById(invoiceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "존재하지 않는 전표입니다."));

        // 2. SCM에서 Supplier Company 정보 조회
        SupplierCompanyResponseDto supplierCompany = supplierCompanyServicePort
                .getSupplierCompanyById(String.valueOf(voucher.getSupplierCompanyId()));

        // 3. SCM에서 Product Order 정보 조회
        ProductOrderInfoResponseDto productOrderInfo = productOrderServicePort
                .getProductOrderItemsById(String.valueOf(voucher.getProductOrder()));

        // 4. Items 변환
        List<APInvoiceItemDto> items = productOrderInfo.getItems().stream()
                .map(item -> new APInvoiceItemDto(
                        item.getItemId(),
                        item.getItemName(),
                        item.getQuantity(),
                        item.getUomName(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ))
                .collect(Collectors.toList());

        // 5. APInvoiceDetailDto 생성
        APInvoiceDetailDto result = new APInvoiceDetailDto(
                voucher.getId(),
                voucher.getVoucherCode(),
                "AP",
                voucher.getStatus().name(),
                voucher.getIssueDate().format(DATE_FORMATTER),
                voucher.getDueDate().format(DATE_FORMATTER),
                supplierCompany.getCompanyName(),
                String.valueOf(voucher.getProductOrder()),
                voucher.getTotalAmount(),
                voucher.getMemo(),
                items
        );

        log.info("AP 전표 상세 정보 조회 성공 - invoiceId: {}, invoiceNumber: {}",
                invoiceId, voucher.getVoucherCode());

        return result;
    }
}
