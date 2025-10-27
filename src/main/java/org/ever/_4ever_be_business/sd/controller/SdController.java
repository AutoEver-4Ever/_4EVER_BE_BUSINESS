package org.ever._4ever_be_business.sd.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.sd.dto.request.*;
import org.ever._4ever_be_business.sd.dto.response.*;
import org.ever._4ever_be_business.sd.service.*;
import org.ever._4ever_be_business.sd.vo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/sd")
@RequiredArgsConstructor
public class SdController {

    private final DashboardStatisticsService dashboardStatisticsService;
    private final SalesAnalyticsService salesAnalyticsService;
    private final SdCustomerService customerService;
    private final SdOrderService sdOrderService;
    private final OrderService orderService;
    private final QuotationService quotationService;

    // ==================== Statistics ====================

    /**
     * 대시보드 통계 조회 (주/월/분기/년)
     */
    @GetMapping("/dashboard/statistics")
    public ApiResponse<DashboardStatisticsDto> getDashboardStatistics() {
        log.info("대시보드 통계 조회 API 호출");
        DashboardStatisticsDto result = dashboardStatisticsService.getDashboardStatistics();
        log.info("대시보드 통계 조회 성공");
        return ApiResponse.success(result, "OK", HttpStatus.OK);
    }

    /**
     * 매출 분석 통계 데이터 조회
     */
    @GetMapping("/analytics/sales")
    public ApiResponse<SalesAnalyticsDto> getSalesAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("매출 분석 통계 조회 API 호출 - startDate: {}, endDate: {}", startDate, endDate);

        if (startDate.isAfter(endDate)) {
            log.warn("시작일이 종료일보다 늦습니다 - startDate: {}, endDate: {}", startDate, endDate);
            return ApiResponse.fail("시작일은 종료일보다 이전이어야 합니다.", HttpStatus.BAD_REQUEST);
        }

        SalesAnalyticsDto result = salesAnalyticsService.getSalesAnalytics(startDate, endDate);
        log.info("매출 분석 통계 조회 성공");
        return ApiResponse.success(result, "매출 통계 데이터를 조회했습니다.", HttpStatus.OK);
    }

    // ==================== Customers ====================

    /**
     * 고객사 목록 조회 (검색 + 페이징)
     */
    @GetMapping("/customers")
    public ApiResponse<CustomerListResponseDto> getCustomerList(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("고객사 목록 조회 API 호출 - status: {}, type: {}, search: {}, page: {}, size: {}",
                status, type, search, page, size);

        CustomerSearchConditionVo condition = new CustomerSearchConditionVo(status, type, search);
        Pageable pageable = PageRequest.of(page, size);
        CustomerListResponseDto result = customerService.getCustomerList(condition, pageable);

        log.info("고객사 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getPage().getTotalElements(), result.getPage().getTotalPages());
        return ApiResponse.success(result, "고객사 목록을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 고객사 등록
     */
    @PostMapping("/customers")
    public ApiResponse<Map<String, String>> createCustomer(@RequestBody CreateCustomerRequestDto dto) {
        log.info("고객사 등록 API 호출 - companyName: {}, businessNumber: {}", dto.getCompanyName(), dto.getBusinessNumber());
        String customerId = customerService.createCustomer(dto);
        log.info("고객사 등록 성공 - customerId: {}", customerId);
        return ApiResponse.success(Map.of("customerId", customerId), "고객사가 등록되었습니다.", HttpStatus.CREATED);
    }

    /**
     * 고객사 상세 정보 조회
     */
    @GetMapping("/customers/{customerId}")
    public ApiResponse<CustomerDetailDto> getCustomerDetail(@PathVariable String customerId) {
        log.info("고객사 상세 정보 조회 API 호출 - customerId: {}", customerId);
        CustomerDetailVo vo = new CustomerDetailVo(customerId);
        CustomerDetailDto result = customerService.getCustomerDetail(vo);
        log.info("고객사 상세 정보 조회 성공 - customerId: {}, customerName: {}", customerId, result.getCustomerName());
        return ApiResponse.success(result, "고객사 상세 정보를 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 고객사 정보 수정
     */
    @PatchMapping("/customers/{customerId}")
    public ApiResponse<Void> updateCustomer(
            @PathVariable String customerId,
            @RequestBody UpdateCustomerRequestDto dto) {
        log.info("고객사 정보 수정 API 호출 - customerId: {}, customerName: {}", customerId, dto.getCustomerName());
        customerService.updateCustomer(customerId, dto);
        log.info("고객사 정보 수정 성공 - customerId: {}", customerId);
        return ApiResponse.success(null, "고객사 정보가 수정되었습니다.", HttpStatus.OK);
    }

    /**
     * 고객사 삭제 (Soft Delete)
     */
    @DeleteMapping("/customers/{customerId}")
    public ApiResponse<Void> deleteCustomer(@PathVariable String customerId) {
        log.info("고객사 삭제 API 호출 - customerId: {}", customerId);
        customerService.deleteCustomer(customerId);
        log.info("고객사 삭제 성공 (Soft Delete) - customerId: {}", customerId);
        return ApiResponse.success(null, "고객사가 삭제되었습니다.", HttpStatus.OK);
    }

    // ==================== Orders ====================

    /**
     * 주문 목록 조회 (검색 + 페이징)
     */
    @GetMapping("/orders")
    public ApiResponse<SalesOrderListResponseDto> getOrderList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("주문 목록 조회 API 호출 - startDate: {}, endDate: {}, status: {}, type: {}, search: {}, page: {}, size: {}",
                startDate, endDate, status, type, search, page, size);

        OrderSearchConditionVo condition = new OrderSearchConditionVo(startDate, endDate, search, type, status);
        Pageable pageable = PageRequest.of(page, size);
        SalesOrderListResponseDto result = sdOrderService.getOrderList(condition, pageable);

        log.info("주문 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getPage().getTotalElements(), result.getPage().getTotalPages());
        return ApiResponse.success(result, "주문 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 주문서 상세 조회
     */
    @GetMapping("/orders/{salesOrderId}")
    public ApiResponse<SalesOrderDetailResponseDto> getOrderDetail(@PathVariable String salesOrderId) {
        log.info("주문서 상세 조회 API 호출 - salesOrderId: {}", salesOrderId);
        SalesOrderDetailResponseDto result = sdOrderService.getOrderDetail(salesOrderId);
        log.info("주문서 상세 조회 성공 - salesOrderId: {}", salesOrderId);
        return ApiResponse.success(result, "주문서 상세 정보를 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 주문 등록
     */
    @PostMapping("/orders")
    public ResponseEntity<Void> registerOrder(@RequestBody RegisterOrderRequest request) {
        log.info("주문 등록 API 호출 - id: {}, name: {}, count: {}", request.getId(), request.getName(), request.getCount());
        orderService.registerOrder(request.getId(), request.getName(), request.getCount());
        log.info("주문 등록 성공 - id: {}", request.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ==================== Quotations ====================

    /**
     * 견적 목록 조회 (검색 + 페이징)
     */
    @GetMapping("/quotations")
    public ApiResponse<Page<QuotationListItemDto>> getQuotationList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("견적 목록 조회 API 호출 - startDate: {}, endDate: {}, status: {}, type: {}, search: {}, sort: {}, page: {}, size: {}",
                startDate, endDate, status, type, search, sort, page, size);

        QuotationSearchConditionVo condition = new QuotationSearchConditionVo(startDate, endDate, status, type, search, sort);
        Pageable pageable = PageRequest.of(page, size);
        Page<QuotationListItemDto> result = quotationService.getQuotationList(condition, pageable);

        log.info("견적 목록 조회 성공 - totalElements: {}, totalPages: {}", result.getTotalElements(), result.getTotalPages());
        return ApiResponse.success(result, "견적 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 견적 상세 조회
     */
    @GetMapping("/quotations/{quotationId}")
    public ApiResponse<QuotationDetailDto> getQuotationDetail(@PathVariable String quotationId) {
        log.info("견적 상세 조회 API 호출 - quotationId: {}", quotationId);
        QuotationDetailVo vo = new QuotationDetailVo(quotationId);
        QuotationDetailDto result = quotationService.getQuotationDetail(vo);
        log.info("견적 상세 조회 성공 - quotationId: {}, quotationNumber: {}", quotationId, result.getQuotationNumber());
        return ApiResponse.success(result, "견적 상세 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 견적서 생성
     */
    @PostMapping("/quotations")
    public ApiResponse<Map<String, String>> createQuotation(@RequestBody CreateQuotationRequestDto dto) {
        log.info("견적서 생성 API 호출 - userId: {}, dueDate: {}, items count: {}",
                dto.getUserId(), dto.getDueDate(), dto.getItems() != null ? dto.getItems().size() : 0);
        String quotationId = quotationService.createQuotation(dto);
        log.info("견적서 생성 성공 - quotationId: {}", quotationId);
        return ApiResponse.success(Map.of("quotationId", quotationId), "견적서가 생성되었습니다.", HttpStatus.CREATED);
    }

    /**
     * 견적서 승인 및 주문 생성
     */
    @PostMapping("/quotations/{quotationId}/approve-order")
    public ApiResponse<Void> approveQuotation(
            @PathVariable String quotationId,
            @RequestBody ApproveOrderRequestDto dto) {
        log.info("견적서 승인 및 주문 생성 API 호출 - quotationId: {}, employeeId: {}", quotationId, dto.getEmployeeId());
        quotationService.approveQuotation(quotationId, dto.getEmployeeId());
        log.info("견적서 승인 및 주문 생성 성공 - quotationId: {}", quotationId);
        return ApiResponse.success(null, "견적서가 승인되고 주문이 생성되었습니다.", HttpStatus.OK);
    }

    /**
     * 견적서 검토 확정
     */
    @PostMapping("/quotations/confirm")
    public ApiResponse<Void> confirmQuotation(@RequestBody ConfirmQuotationRequestDto dto) {
        log.info("견적서 검토 확정 API 호출 - quotationId: {}", dto.getQuotationId());
        quotationService.confirmQuotation(dto.getQuotationId());
        log.info("견적서 검토 확정 성공 - quotationId: {}", dto.getQuotationId());
        return ApiResponse.success(null, "견적서가 검토 확정되었습니다.", HttpStatus.OK);
    }

    /**
     * 재고 확인
     */
    @PostMapping("/quotations/inventory/check")
    public ApiResponse<InventoryCheckResponseDto> checkInventory(@RequestBody InventoryCheckRequestDto requestDto) {
        log.info("재고 확인 API 호출 - items count: {}", requestDto.getItems() != null ? requestDto.getItems().size() : 0);
        InventoryCheckResponseDto result = quotationService.checkInventory(requestDto);
        log.info("재고 확인 성공 - items count: {}", result.getItems().size());
        return ApiResponse.success(result, "재고 확인을 완료했습니다.", HttpStatus.OK);
    }

    // ==================== DTOs ====================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterOrderRequest {
        private Long id;
        private String name;
        private Integer count;
    }
}
