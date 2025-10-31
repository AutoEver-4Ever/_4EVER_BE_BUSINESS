package org.ever._4ever_be_business.fcm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.fcm.dto.request.ARInvoiceSearchConditionDto;
import org.ever._4ever_be_business.fcm.dto.response.ARInvoiceDetailDto;
import org.ever._4ever_be_business.fcm.dto.response.ARInvoiceItemDto;
import org.ever._4ever_be_business.fcm.dto.response.ARInvoiceListItemDto;
import org.ever._4ever_be_business.fcm.integration.dto.OrderItemsResponseDto;
import org.ever._4ever_be_business.fcm.integration.port.OrderServicePort;
import org.ever._4ever_be_business.fcm.service.ARInvoiceService;
import org.ever._4ever_be_business.voucher.entity.SalesVoucher;
import org.ever._4ever_be_business.voucher.enums.SalesVoucherStatus;
import org.ever._4ever_be_business.voucher.repository.SalesVoucherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ARInvoiceServiceImpl implements ARInvoiceService {

    private final SalesVoucherRepository salesVoucherRepository;
    private final OrderServicePort orderServicePort;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional(readOnly = true)
    public Page<ARInvoiceListItemDto> getARInvoiceList(String company, LocalDate startDate, LocalDate endDate, int page, int size) {
        log.info("AR 전표 목록 조회 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, startDate, endDate, page, size);

        // 검색 조건 생성
        ARInvoiceSearchConditionDto condition = new ARInvoiceSearchConditionDto(company, startDate, endDate);

        // 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 목록 조회
        Page<ARInvoiceListItemDto> result = salesVoucherRepository.findARInvoiceList(condition, pageable);

        log.info("AR 전표 목록 조회 완료 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ARInvoiceDetailDto getARInvoiceDetail(String invoiceId) {
        log.info("AR 전표 상세 정보 조회 - invoiceId: {}", invoiceId);

        // 1. SalesVoucher 조회
        SalesVoucher voucher = salesVoucherRepository.findById(invoiceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "존재하지 않는 전표입니다."));

        // 2. SCM에서 Order Items 정보 조회
        OrderItemsResponseDto orderItemsInfo = orderServicePort.getOrderItemsById(voucher.getOrder().getId());

        // 3. Items 변환
        List<ARInvoiceItemDto> items = orderItemsInfo.getItems().stream()
                .map(item -> new ARInvoiceItemDto(
                        item.getItemId(),
                        item.getItemName(),
                        item.getQuantity(),
                        item.getUomName(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ))
                .collect(Collectors.toList());

        // 4. ARInvoiceDetailDto 생성
        ARInvoiceDetailDto result = new ARInvoiceDetailDto(
                voucher.getId(),
                voucher.getVoucherCode(),
                "AR",
                voucher.getStatus().name(),
                voucher.getIssueDate().format(DATE_FORMATTER),
                voucher.getDueDate().format(DATE_FORMATTER),
                voucher.getCustomerCompany().getCompanyName(),
                voucher.getOrder().getOrderCode(),
                voucher.getTotalAmount(),
                voucher.getMemo(),
                items
        );

        log.info("AR 전표 상세 정보 조회 성공 - invoiceId: {}, invoiceNumber: {}",
                invoiceId, voucher.getVoucherCode());

        return result;
    }

    @Override
    @Transactional
    public void updateARInvoice(String invoiceId, String status, String dueDate, String memo) {
        log.info("AR 전표 정보 업데이트 - invoiceId: {}, status: {}, dueDate: {}, memo: {}",
                invoiceId, status, dueDate, memo);

        // 1. SalesVoucher 조회
        SalesVoucher voucher = salesVoucherRepository.findById(invoiceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "존재하지 않는 전표입니다."));

        // 2. 파라미터 변환
        SalesVoucherStatus newStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                newStatus = SalesVoucherStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "유효하지 않은 상태 코드입니다: " + status);
            }
        }

        LocalDateTime newDueDate = null;
        if (dueDate != null && !dueDate.isBlank()) {
            try {
                newDueDate = LocalDate.parse(dueDate, DATE_FORMATTER).atStartOfDay();
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "유효하지 않은 날짜 형식입니다: " + dueDate);
            }
        }

        // 3. 엔티티 업데이트
        voucher.updateARInvoice(newStatus, newDueDate, memo);

        // 4. 저장 (더티 체킹으로 자동 저장되지만 명시적으로 호출)
        salesVoucherRepository.save(voucher);

        log.info("AR 전표 정보 업데이트 완료 - invoiceId: {}", invoiceId);
    }

    @Override
    @Transactional
    public void completeReceivable(String invoiceId) {
        log.info("AR 전표 미수 처리 완료 - invoiceId: {}", invoiceId);

        // 1. SalesVoucher 조회
        SalesVoucher voucher = salesVoucherRepository.findById(invoiceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "존재하지 않는 전표입니다."));

        // 2. 상태를 PAID로 변경
        voucher.updateStatus(SalesVoucherStatus.PAID);

        // 3. 저장
        salesVoucherRepository.save(voucher);

        log.info("AR 전표 미수 처리 완료 성공 - invoiceId: {}, status: PAID", invoiceId);
    }
}
