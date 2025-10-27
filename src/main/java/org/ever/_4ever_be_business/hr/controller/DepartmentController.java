package org.ever._4ever_be_business.hr.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.hr.dto.response.DepartmentListItemDto;
import org.ever._4ever_be_business.hr.dto.response.DepartmentListResponseDto;
import org.ever._4ever_be_business.hr.service.DepartmentService;
import org.ever._4ever_be_business.sd.dto.response.PageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/hrm/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 부서 목록 조회
     *
     * @param status 상태 필터
     * @param page   페이지 번호 (0부터 시작)
     * @param size   페이지 크기
     * @return ApiResponse<DepartmentListResponseDto>
     */
    @GetMapping
    public ApiResponse<DepartmentListResponseDto> getDepartmentList(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("부서 목록 조회 API 호출 - status: {}, page: {}, size: {}", status, page, size);

        // 1. 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 2. Service 호출
        Page<DepartmentListItemDto> pageResult = departmentService.getDepartmentList(status, pageable);

        // 3. Response DTO로 변환
        PageInfo pageInfo = new PageInfo(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.hasNext()
        );

        DepartmentListResponseDto responseDto = new DepartmentListResponseDto(
                (int) pageResult.getTotalElements(),
                pageResult.getContent(),
                pageInfo
        );

        log.info("부서 목록 조회 성공 - total: {}, size: {}",
                pageResult.getTotalElements(), pageResult.getContent().size());

        return ApiResponse.success(responseDto, "부서 목록을 조회했습니다.", HttpStatus.OK);
    }
}
