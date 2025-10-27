package org.ever._4ever_be_business.fcm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.fcm.dto.response.FcmStatisticsDto;
import org.ever._4ever_be_business.fcm.dto.response.PurchaseStatementDetailDto;
import org.ever._4ever_be_business.fcm.dto.response.PurchaseStatementListItemDto;
import org.ever._4ever_be_business.fcm.dto.response.SalesStatementDetailDto;
import org.ever._4ever_be_business.fcm.dto.response.SalesStatementListItemDto;
import org.ever._4ever_be_business.fcm.service.FcmStatisticsService;
import org.ever._4ever_be_business.fcm.service.PurchaseStatementService;
import org.ever._4ever_be_business.fcm.service.SalesStatementService;
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
public class FcmStatisticsController {

    private final FcmStatisticsService fcmStatisticsService;
    private final SalesStatementService salesStatementService;
    private final PurchaseStatementService purchaseStatementService;

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

    /**
     * 매출전표 목록 조회
     *
     * @param company 거래처명 (optional)
     * @param startDate 시작일 (optional)
     * @param endDate 종료일 (optional)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return ApiResponse<Page<SalesStatementListItemDto>>
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
     *
     * @param statementId 전표 ID
     * @return ApiResponse<SalesStatementDetailDto>
     */
    @GetMapping("/statement/as/{statementId}")
    public ApiResponse<SalesStatementDetailDto> getSalesStatementDetail(@PathVariable String statementId) {
        log.info("매출전표 상세 조회 API 호출 - statementId: {}", statementId);

        SalesStatementDetailDto result = salesStatementService.getSalesStatementDetail(statementId);

        log.info("매출전표 상세 조회 성공 - statementId: {}, invoiceCode: {}",
                statementId, result.getInvoiceCode());

        return ApiResponse.success(result, "매출 전표 상세 정보 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 매입전표 상세 조회
     *
     * @param statementId 전표 ID
     * @return ApiResponse<PurchaseStatementDetailDto>
     */
    @GetMapping("/statement/ap/{statementId}")
    public ApiResponse<PurchaseStatementDetailDto> getPurchaseStatementDetail(@PathVariable String statementId) {
        log.info("매입전표 상세 조회 API 호출 - statementId: {}", statementId);

        PurchaseStatementDetailDto result = purchaseStatementService.getPurchaseStatementDetail(statementId);

        log.info("매입전표 상세 조회 성공 - statementId: {}, invoiceCode: {}",
                statementId, result.getInvoiceCode());

        return ApiResponse.success(result, "매입 전표 상세 정보 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 매입전표 목록 조회
     *
     * @param company 공급업체명 (optional)
     * @param status 전표 상태 (optional)
     * @param startDate 시작일 (optional)
     * @param endDate 종료일 (optional)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return ApiResponse<Page<PurchaseStatementListItemDto>>
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
}
