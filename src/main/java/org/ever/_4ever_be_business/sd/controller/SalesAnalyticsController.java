package org.ever._4ever_be_business.sd.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.sd.dto.response.SalesAnalyticsDto;
import org.ever._4ever_be_business.sd.service.SalesAnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/sd/analytics/sales")
@RequiredArgsConstructor
public class SalesAnalyticsController {

    private final SalesAnalyticsService salesAnalyticsService;

    /**
     * 매출 분석 통계 데이터 조회
     *
     * @param startDate 시작일 (yyyy-MM-dd)
     * @param endDate   종료일 (yyyy-MM-dd)
     * @return ApiResponse<SalesAnalyticsDto>
     */
    @GetMapping
    public ApiResponse<SalesAnalyticsDto> getSalesAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("매출 분석 통계 조회 API 호출 - startDate: {}, endDate: {}", startDate, endDate);

        // Validation
        if (startDate.isAfter(endDate)) {
            log.warn("시작일이 종료일보다 늦습니다 - startDate: {}, endDate: {}", startDate, endDate);
            return ApiResponse.fail("시작일은 종료일보다 이전이어야 합니다.", HttpStatus.BAD_REQUEST);
        }

        SalesAnalyticsDto result = salesAnalyticsService.getSalesAnalytics(startDate, endDate);

        log.info("매출 분석 통계 조회 성공");

        return ApiResponse.success(result, "매출 통계 데이터를 조회했습니다.", HttpStatus.OK);
    }
}
