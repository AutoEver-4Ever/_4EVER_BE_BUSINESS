package org.ever._4ever_be_business.hr.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.hr.dto.response.DepartmentDetailDto;
import org.ever._4ever_be_business.hr.dto.response.DepartmentListItemDto;
import org.ever._4ever_be_business.hr.dto.response.PositionDetailDto;
import org.ever._4ever_be_business.hr.dto.response.PositionListItemDto;
import org.ever._4ever_be_business.hr.service.DepartmentService;
import org.ever._4ever_be_business.hr.service.PositionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/hr/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final PositionService positionService;
    private final DepartmentService departmentService;

    /**
     * 직급 목록 조회
     *
     * @return ApiResponse<List<PositionListItemDto>>
     */
    @GetMapping("/position")
    public ApiResponse<List<PositionListItemDto>> getPositionList() {
        log.info("직급 목록 조회 API 호출");

        List<PositionListItemDto> result = positionService.getPositionList();

        log.info("직급 목록 조회 성공 - count: {}", result.size());

        return ApiResponse.success(result, "직급 목록을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 직급 상세 정보 조회
     *
     * @param positionId 직급 ID
     * @return ApiResponse<PositionDetailDto>
     */
    @GetMapping("/position/{positionId}")
    public ApiResponse<PositionDetailDto> getPositionDetail(@PathVariable String positionId) {
        log.info("직급 상세 정보 조회 API 호출 - positionId: {}", positionId);

        PositionDetailDto result = positionService.getPositionDetail(positionId);

        log.info("직급 상세 정보 조회 성공 - positionId: {}, positionName: {}, headCount: {}",
                positionId, result.getPositionName(), result.getHeadCount());

        return ApiResponse.success(result, "직급 상세 정보를 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 부서 목록 조회
     *
     * @param status 부서 상태 (ACTIVE/INACTIVE)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return ApiResponse<Page<DepartmentListItemDto>>
     */
    @GetMapping("/department")
    public ApiResponse<Page<DepartmentListItemDto>> getDepartmentList(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("부서 목록 조회 API 호출 - status: {}, page: {}, size: {}", status, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<DepartmentListItemDto> result = departmentService.getDepartmentList(status, pageable);

        log.info("부서 목록 조회 성공 - total: {}, size: {}", result.getTotalElements(), result.getContent().size());

        return ApiResponse.success(result, "부서 목록을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 부서 상세 정보 조회
     *
     * @param departmentId 부서 ID
     * @return ApiResponse<DepartmentDetailDto>
     */
    @GetMapping("/department/{departmentId}")
    public ApiResponse<DepartmentDetailDto> getDepartmentDetail(@PathVariable String departmentId) {
        log.info("부서 상세 정보 조회 API 호출 - departmentId: {}", departmentId);

        DepartmentDetailDto result = departmentService.getDepartmentDetail(departmentId);

        log.info("부서 상세 정보 조회 성공 - departmentId: {}, departmentName: {}, headcount: {}",
                departmentId, result.getDepartmentName(), result.getHeadcount());

        return ApiResponse.success(result, "부서 상세 정보 조회에 성공했습니다.", HttpStatus.OK);
    }
}
