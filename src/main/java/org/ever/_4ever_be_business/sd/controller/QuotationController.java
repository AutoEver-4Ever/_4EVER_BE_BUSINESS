package org.ever._4ever_be_business.sd.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.sd.dto.request.ApproveOrderRequestDto;
import org.ever._4ever_be_business.sd.dto.request.ConfirmQuotationRequestDto;
import org.ever._4ever_be_business.sd.dto.request.CreateQuotationRequestDto;
import org.ever._4ever_be_business.sd.dto.request.InventoryCheckRequestDto;
import org.ever._4ever_be_business.sd.dto.response.InventoryCheckResponseDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationDetailDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationListItemDto;
import org.ever._4ever_be_business.sd.service.QuotationService;
import org.ever._4ever_be_business.sd.vo.QuotationDetailVo;
import org.ever._4ever_be_business.sd.vo.QuotationSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/sd/quotations")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;

    /**
     * 견적 목록 조회 (검색 + 페이징)
     *
     * @param startDate 시작일 (YYYY-MM-DD)
     * @param endDate   종료일 (YYYY-MM-DD)
     * @param status    상태 필터 (PENDING, REVIEW, APPROVAL, REJECTED, ALL)
     * @param type      검색 타입 (quotationNumber, customerName, managerName)
     * @param search    검색어
     * @param sort      정렬 (asc, desc)
     * @param page      페이지 번호 (0부터 시작)
     * @param size      페이지 크기
     * @return ApiResponse<Page<QuotationListItemDto>>
     */
    @GetMapping
    public ApiResponse<Page<QuotationListItemDto>> getQuotationList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("견적 목록 조회 API 호출 - startDate: {}, endDate: {}, status: {}, type: {}, search: {}, sort: {}, page: {}, size: {}",
                startDate, endDate, status, type, search, sort, page, size);

        // 1. 검색 조건 VO 생성
        QuotationSearchConditionVo condition = new QuotationSearchConditionVo(
                startDate, endDate, status, type, search, sort
        );

        // 2. 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. Service 호출
        Page<QuotationListItemDto> result = quotationService.getQuotationList(condition, pageable);

        log.info("견적 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return ApiResponse.success(result, "견적 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 견적 상세 조회
     *
     * @param quotationId 견적 ID
     * @return ApiResponse<QuotationDetailDto>
     */
    @GetMapping("/{quotationId}")
    public ApiResponse<QuotationDetailDto> getQuotationDetail(@PathVariable String quotationId) {
        log.info("견적 상세 조회 API 호출 - quotationId: {}", quotationId);

        // 1. VO 생성
        QuotationDetailVo vo = new QuotationDetailVo(quotationId);

        // 2. Service 호출
        QuotationDetailDto result = quotationService.getQuotationDetail(vo);

        log.info("견적 상세 조회 성공 - quotationId: {}, quotationNumber: {}",
                quotationId, result.getQuotationNumber());

        return ApiResponse.success(result, "견적 상세 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 견적서 생성
     *
     * @param dto 견적서 생성 요청
     * @return ApiResponse<Map<String, String>>
     */
    @PostMapping
    public ApiResponse<Map<String, String>> createQuotation(@RequestBody CreateQuotationRequestDto dto) {
        log.info("견적서 생성 API 호출 - userId: {}, dueDate: {}, items count: {}",
                dto.getUserId(), dto.getDueDate(), dto.getItems() != null ? dto.getItems().size() : 0);

        String quotationId = quotationService.createQuotation(dto);

        log.info("견적서 생성 성공 - quotationId: {}", quotationId);

        return ApiResponse.success(
                Map.of("quotationId", quotationId),
                "견적서가 생성되었습니다.",
                HttpStatus.CREATED
        );
    }

    /**
     * 견적서 승인 및 주문 생성
     *
     * @param quotationId 견적서 ID
     * @param dto 승인 요청 정보 (employeeId 포함)
     * @return ApiResponse<Void>
     */
    @PostMapping("/{quotationId}/approve-order")
    public ApiResponse<Void> approveQuotation(
            @PathVariable String quotationId,
            @RequestBody ApproveOrderRequestDto dto
    ) {
        log.info("견적서 승인 및 주문 생성 API 호출 - quotationId: {}, employeeId: {}",
                quotationId, dto.getEmployeeId());

        quotationService.approveQuotation(quotationId, dto.getEmployeeId());

        log.info("견적서 승인 및 주문 생성 성공 - quotationId: {}", quotationId);

        return ApiResponse.success(null, "견적서가 승인되고 주문이 생성되었습니다.", HttpStatus.OK);
    }

    /**
     * 견적서 검토 확정
     *
     * @param dto 견적서 확정 요청
     * @return ApiResponse<Void>
     */
    @PostMapping("/confirm")
    public ApiResponse<Void> confirmQuotation(@RequestBody ConfirmQuotationRequestDto dto) {
        log.info("견적서 검토 확정 API 호출 - quotationId: {}", dto.getQuotationId());

        quotationService.confirmQuotation(dto.getQuotationId());

        log.info("견적서 검토 확정 성공 - quotationId: {}", dto.getQuotationId());

        return ApiResponse.success(null, "견적서가 검토 확정되었습니다.", HttpStatus.OK);
    }

    /**
     * 재고 확인
     *
     * @param requestDto 재고 확인 요청
     * @return ApiResponse<InventoryCheckResponseDto>
     */
    @PostMapping("/inventory/check")
    public ApiResponse<InventoryCheckResponseDto> checkInventory(@RequestBody InventoryCheckRequestDto requestDto) {
        log.info("재고 확인 API 호출 - items count: {}", requestDto.getItems() != null ? requestDto.getItems().size() : 0);

        InventoryCheckResponseDto result = quotationService.checkInventory(requestDto);

        log.info("재고 확인 성공 - items count: {}", result.getItems().size());

        return ApiResponse.success(result, "재고 확인을 완료했습니다.", HttpStatus.OK);
    }
}
