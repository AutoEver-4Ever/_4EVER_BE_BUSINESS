package org.ever._4ever_be_business.fcm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.fcm.dto.request.UpdateARInvoiceDto;
import org.ever._4ever_be_business.fcm.dto.response.ARInvoiceDetailDto;
import org.ever._4ever_be_business.fcm.dto.response.ARInvoiceListItemDto;
import org.ever._4ever_be_business.fcm.service.ARInvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/fcm/invoice/ar")
@RequiredArgsConstructor
public class ARInvoiceController {

    private final ARInvoiceService arInvoiceService;

    /**
     * AR 전표 목록 조회
     *
     * @param company 고객사명 (검색 필터, optional)
     * @param startDate 시작일 (검색 필터, optional)
     * @param endDate 종료일 (검색 필터, optional)
     * @param page 페이지 번호 (default: 0)
     * @param size 페이지 크기 (default: 10)
     * @return ApiResponse<Page<ARInvoiceListItemDto>>
     */
    @GetMapping
    public ApiResponse<Page<ARInvoiceListItemDto>> getARInvoiceList(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("AR 전표 목록 조회 API 호출 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, startDate, endDate, page, size);

        Page<ARInvoiceListItemDto> result = arInvoiceService.getARInvoiceList(company, startDate, endDate, page, size);

        log.info("AR 전표 목록 조회 성공 - totalElements: {}", result.getTotalElements());

        return ApiResponse.success(result, "매출 전표 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * AR 전표 상세 정보 조회
     *
     * @param invoiceId 전표 ID
     * @return ApiResponse<ARInvoiceDetailDto>
     */
    @GetMapping("/{invoiceId}")
    public ApiResponse<ARInvoiceDetailDto> getARInvoiceDetail(
            @PathVariable String invoiceId
    ) {
        log.info("AR 전표 상세 정보 조회 API 호출 - invoiceId: {}", invoiceId);

        ARInvoiceDetailDto result = arInvoiceService.getARInvoiceDetail(invoiceId);

        log.info("AR 전표 상세 정보 조회 성공 - invoiceId: {}, invoiceNumber: {}",
                invoiceId, result.getInvoiceNumber());

        return ApiResponse.success(result, "매출 전표 상세 정보 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * AR 전표 정보 업데이트
     *
     * @param invoiceId 전표 ID
     * @param requestDto 업데이트할 정보 (status, dueDate, memo)
     * @return ApiResponse<Void>
     */
    @PatchMapping("/{invoiceId}")
    public ApiResponse<Void> updateARInvoice(
            @PathVariable String invoiceId,
            @RequestBody UpdateARInvoiceDto requestDto
    ) {
        log.info("AR 전표 정보 업데이트 API 호출 - invoiceId: {}, status: {}, dueDate: {}, memo: {}",
                invoiceId, requestDto.getStatus(), requestDto.getDueDate(), requestDto.getMemo());

        arInvoiceService.updateARInvoice(
                invoiceId,
                requestDto.getStatus(),
                requestDto.getDueDate(),
                requestDto.getMemo()
        );

        log.info("AR 전표 정보 업데이트 성공 - invoiceId: {}", invoiceId);

        return ApiResponse.success(null, "매출 전표 정보가 업데이트되었습니다.", HttpStatus.OK);
    }
}
