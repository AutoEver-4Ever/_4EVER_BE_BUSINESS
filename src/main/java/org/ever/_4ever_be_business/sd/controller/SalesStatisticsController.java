package org.ever._4ever_be_business.sd.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.sd.dto.response.SalesStatisticsDto;
import org.ever._4ever_be_business.sd.service.SalesStatisticsService;
import org.ever._4ever_be_business.sd.vo.StatisticsSearchConditionVo;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/sd")
@RequiredArgsConstructor
public class SalesStatisticsController {

    private final SalesStatisticsService salesStatisticsService;

    /**
     * 기간별 매출 통계 조회 (주간/월간/분기/연간)
     *
     * @return ApiResponse<DashboardStatisticsDto>
     */
    @GetMapping("/statistics")
    public ApiResponse<org.ever._4ever_be_business.sd.dto.response.DashboardStatisticsDto> getPeriodStatistics() {
        log.info("기간별 매출 통계 조회 요청");

        // Service 호출
        org.ever._4ever_be_business.sd.dto.response.DashboardStatisticsDto result =
                salesStatisticsService.getPeriodStatistics();

        log.info("기간별 매출 통계 조회 성공");

        return ApiResponse.success(result, "OK", HttpStatus.OK);
    }
}
