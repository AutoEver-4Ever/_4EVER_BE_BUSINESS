package org.ever._4ever_be_business.sd.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.sd.dto.response.DashboardStatisticsDto;
import org.ever._4ever_be_business.sd.service.DashboardStatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sd/dashboard")
@RequiredArgsConstructor
public class DashboardStatisticsController {

    private final DashboardStatisticsService dashboardStatisticsService;

    /**
     * 대시보드 통계 조회 (주/월/분기/년)
     *
     * @return ApiResponse<DashboardStatisticsDto>
     */
    @GetMapping("/statistics")
    public ApiResponse<DashboardStatisticsDto> getDashboardStatistics() {
        log.info("대시보드 통계 조회 API 호출");

        DashboardStatisticsDto result = dashboardStatisticsService.getDashboardStatistics();

        log.info("대시보드 통계 조회 성공");

        return ApiResponse.success(result, "OK", HttpStatus.OK);
    }
}
