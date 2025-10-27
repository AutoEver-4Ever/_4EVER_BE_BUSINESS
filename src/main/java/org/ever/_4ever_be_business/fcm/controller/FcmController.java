package org.ever._4ever_be_business.fcm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.fcm.dto.request.UpdateARInvoiceDto;
import org.ever._4ever_be_business.fcm.dto.request.UpdateVoucherStatusDto;
import org.ever._4ever_be_business.fcm.dto.response.*;
import org.ever._4ever_be_business.fcm.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final FcmStatisticsService fcmStatisticsService;
    private final SalesStatementService salesStatementService;
    private final PurchaseStatementService purchaseStatementService;
    private final APInvoiceService apInvoiceService;
    private final ARInvoiceService arInvoiceService;
    private final VoucherStatusService voucherStatusService;

    // ==================== Statistics ====================

    /**
     * 재무관리 통계 조회 (주/월/분기/년)
     *
     * @return ApiResponse<FcmStatisticsDto>
     */
    @GetMapping("/statistics")
    public ApiResponse<FcmStatisticsDto> getFcmStatistics() {
        log.info("재무관리 통계 조회 API 호출");
        FcmStatisticsDto result = fcmStatisticsService.getFcmStatistics();
        log.info("재무관리 통계 조회 성공");
        return ApiResponse.success(result, "재무 통계 데이터를 성공적으로 조회했습니다.", HttpStatus.OK);
    }

    // ==================== Sales Statements (매출전표) ====================

    /**
     * 매출전표 목록 조회
     */
    @GetMapping("/statement/as")
    public ApiResponse<Page<SalesStatementListItemDto>> getSalesStatementList(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("매출전표 목록 조회 API 호출 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, startDate, endDate, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<SalesStatementListItemDto> result = salesStatementService.getSalesStatementList(company, startDate, endDate, pageable);

        log.info("매출전표 목록 조회 성공 - total: {}, size: {}", result.getTotalElements(), result.getContent().size());
        return ApiResponse.success(result, "매출 전표 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 매출전표 상세 조회
     */
    @GetMapping("/statement/as/{statementId}")
    public ApiResponse<SalesStatementDetailDto> getSalesStatementDetail(@PathVariable String statementId) {
        log.info("매출전표 상세 조회 API 호출 - statementId: {}", statementId);
        SalesStatementDetailDto result = salesStatementService.getSalesStatementDetail(statementId);
        log.info("매출전표 상세 조회 성공 - statementId: {}, invoiceCode: {}", statementId, result.getInvoiceCode());
        return ApiResponse.success(result, "매출 전표 상세 정보 조회에 성공했습니다.", HttpStatus.OK);
    }

    // ==================== Purchase Statements (매입전표) ====================

    /**
     * 매입전표 목록 조회
     */
    @GetMapping("/statement/ap")
    public ApiResponse<Page<PurchaseStatementListItemDto>> getPurchaseStatementList(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("매입전표 목록 조회 API 호출 - company: {}, status: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, status, startDate, endDate, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<PurchaseStatementListItemDto> result = purchaseStatementService.getPurchaseStatementList(
                company, status, startDate, endDate, pageable
        );

        log.info("매입전표 목록 조회 성공 - total: {}, size: {}", result.getTotalElements(), result.getContent().size());
        return ApiResponse.success(result, "매입 전표 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 매입전표 상세 조회
     */
    @GetMapping("/statement/ap/{statementId}")
    public ApiResponse<PurchaseStatementDetailDto> getPurchaseStatementDetail(@PathVariable String statementId) {
        log.info("매입전표 상세 조회 API 호출 - statementId: {}", statementId);
        PurchaseStatementDetailDto result = purchaseStatementService.getPurchaseStatementDetail(statementId);
        log.info("매입전표 상세 조회 성공 - statementId: {}, invoiceCode: {}", statementId, result.getInvoiceCode());
        return ApiResponse.success(result, "매입 전표 상세 정보 조회에 성공했습니다.", HttpStatus.OK);
    }

    // ==================== AP Invoices (매입 전표) ====================

    /**
     * AP 전표 상세 정보 조회
     */
    @GetMapping("/invoice/ap/{invoiceId}")
    public ApiResponse<APInvoiceDetailDto> getAPInvoiceDetail(@PathVariable String invoiceId) {
        log.info("AP 전표 상세 정보 조회 API 호출 - invoiceId: {}", invoiceId);
        APInvoiceDetailDto result = apInvoiceService.getAPInvoiceDetail(invoiceId);
        log.info("AP 전표 상세 정보 조회 성공 - invoiceId: {}, invoiceNumber: {}", invoiceId, result.getInvoiceNumber());
        return ApiResponse.success(result, "매입 전표 상세 정보 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 바우처 상태 수동 업데이트
     */
    @PostMapping("/invoice/ap/receivable/request")
    public ApiResponse<Void> updateVoucherStatus(@RequestBody UpdateVoucherStatusDto requestDto) {
        log.info("바우처 상태 업데이트 API 호출 - voucherId: {}, statusCode: {}", requestDto.getVoucherId(), requestDto.getStatusCode());
        voucherStatusService.updateVoucherStatus(requestDto.getVoucherId(), requestDto.getStatusCode());
        log.info("바우처 상태 업데이트 성공 - voucherId: {}", requestDto.getVoucherId());
        return ApiResponse.success(null, "바우처 상태가 업데이트되었습니다.", HttpStatus.OK);
    }

    // ==================== AR Invoices (매출 전표) ====================

    /**
     * AR 전표 목록 조회
     */
    @GetMapping("/invoice/ar")
    public ApiResponse<Page<ARInvoiceListItemDto>> getARInvoiceList(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("AR 전표 목록 조회 API 호출 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, startDate, endDate, page, size);

        Page<ARInvoiceListItemDto> result = arInvoiceService.getARInvoiceList(company, startDate, endDate, page, size);
        log.info("AR 전표 목록 조회 성공 - totalElements: {}", result.getTotalElements());
        return ApiResponse.success(result, "매출 전표 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * AR 전표 상세 정보 조회
     */
    @GetMapping("/invoice/ar/{invoiceId}")
    public ApiResponse<ARInvoiceDetailDto> getARInvoiceDetail(@PathVariable String invoiceId) {
        log.info("AR 전표 상세 정보 조회 API 호출 - invoiceId: {}", invoiceId);
        ARInvoiceDetailDto result = arInvoiceService.getARInvoiceDetail(invoiceId);
        log.info("AR 전표 상세 정보 조회 성공 - invoiceId: {}, invoiceNumber: {}", invoiceId, result.getInvoiceNumber());
        return ApiResponse.success(result, "매출 전표 상세 정보 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * AR 전표 정보 업데이트
     */
    @PatchMapping("/invoice/ar/{invoiceId}")
    public ApiResponse<Void> updateARInvoice(
            @PathVariable String invoiceId,
            @RequestBody UpdateARInvoiceDto requestDto) {
        log.info("AR 전표 정보 업데이트 API 호출 - invoiceId: {}, status: {}, dueDate: {}, memo: {}",
                invoiceId, requestDto.getStatus(), requestDto.getDueDate(), requestDto.getMemo());

        arInvoiceService.updateARInvoice(invoiceId, requestDto.getStatus(), requestDto.getDueDate(), requestDto.getMemo());
        log.info("AR 전표 정보 업데이트 성공 - invoiceId: {}", invoiceId);
        return ApiResponse.success(null, "매출 전표 정보가 업데이트되었습니다.", HttpStatus.OK);
    }
}
