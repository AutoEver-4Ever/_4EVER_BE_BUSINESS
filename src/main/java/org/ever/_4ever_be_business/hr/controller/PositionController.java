package org.ever._4ever_be_business.hr.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.hr.dto.response.PositionListItemDto;
import org.ever._4ever_be_business.hr.service.PositionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/hrm/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    /**
     * 직급 목록 조회
     *
     * @return ApiResponse<List<PositionListItemDto>>
     */
    @GetMapping
    public ApiResponse<List<PositionListItemDto>> getPositionList() {
        log.info("직급 목록 조회 API 호출");

        List<PositionListItemDto> result = positionService.getPositionList();

        log.info("직급 목록 조회 성공 - count: {}", result.size());

        return ApiResponse.success(result, "직급 목록을 조회했습니다.", HttpStatus.OK);
    }
}
