package org.ever._4ever_be_business.sd.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.sd.dto.response.SalesOrderDetailResponseDto;
import org.ever._4ever_be_business.sd.dto.response.SalesOrderListResponseDto;
import org.ever._4ever_be_business.sd.service.SdOrderService;
import org.ever._4ever_be_business.sd.vo.OrderSearchConditionVo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/sd/orders")
@RequiredArgsConstructor
public class SdOrderController {

    private final SdOrderService orderService;

    /**
     * 주문 목록 조회 (검색 + 페이징)
     *
     * @param startDate 시작일 (YYYY-MM-DD)
     * @param endDate   종료일 (YYYY-MM-DD)
     * @param search    검색어
     * @param type      검색 타입 (salesOrderNumber, customerName, managerName)
     * @param status    상태 필터 (ALL, MATERIAL_PREPARATION, IN_PRODUCTION, READY_FOR_SHIPMENT, DELIVERING, DELIVERED)
     * @param page      페이지 번호 (0부터 시작)
     * @param size      페이지 크기
     * @return ApiResponse<SalesOrderListResponseDto>
     */
    @GetMapping
    public ApiResponse<SalesOrderListResponseDto> getOrderList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("주문 목록 조회 API 호출 - startDate: {}, endDate: {}, status: {}, type: {}, search: {}, page: {}, size: {}",
                startDate, endDate, status, type, search, page, size);

        // 1. 검색 조건 VO 생성
        OrderSearchConditionVo condition = new OrderSearchConditionVo(
                startDate, endDate, search, type, status
        );

        // 2. 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. Service 호출
        SalesOrderListResponseDto result = orderService.getOrderList(condition, pageable);

        log.info("주문 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getPage().getTotalElements(), result.getPage().getTotalPages());

        return ApiResponse.success(result, "주문 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 주문서 상세 조회
     *
     * @param salesOrderId 주문서 ID
     * @return ApiResponse<SalesOrderDetailResponseDto>
     */
    @GetMapping("/{salesOrderId}")
    public ApiResponse<SalesOrderDetailResponseDto> getOrderDetail(
            @PathVariable String salesOrderId
    ) {
        log.info("주문서 상세 조회 API 호출 - salesOrderId: {}", salesOrderId);

        SalesOrderDetailResponseDto result = orderService.getOrderDetail(salesOrderId);

        log.info("주문서 상세 조회 성공 - salesOrderId: {}", salesOrderId);

        return ApiResponse.success(result, "주문서 상세 정보를 조회했습니다.", HttpStatus.OK);
    }
}
