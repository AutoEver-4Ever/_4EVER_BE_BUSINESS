package org.ever._4ever_be_business.hr.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.hr.dto.response.HRStatisticsResponseDto;
import org.ever._4ever_be_business.hr.service.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/hrm/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * HR 대시보드 통계 조회
     *
     * @return ApiResponse<HRStatisticsResponseDto>
     */
    @GetMapping
    public ApiResponse<HRStatisticsResponseDto> getHRStatistics() {
        log.info("HR 대시보드 통계 조회 API 호출");

        HRStatisticsResponseDto result = statisticsService.getHRStatistics();

        log.info("HR 대시보드 통계 조회 성공");

        return ApiResponse.success(result, "대시보드 정보를 성공적으로 조회했습니다.", HttpStatus.OK);
    }
}
