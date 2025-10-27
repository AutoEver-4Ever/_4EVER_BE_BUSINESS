package org.ever._4ever_be_business.hr.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.hr.dto.request.TrainingRequestDto;
import org.ever._4ever_be_business.hr.dto.request.UpdateEmployeeRequestDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeDetailDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeListItemDto;
import org.ever._4ever_be_business.hr.service.EmployeeService;
import org.ever._4ever_be_business.hr.vo.EmployeeListSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/hrm/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * 직원 목록 조회
     *
     * @param department 부서명 필터
     * @param position   직급 필터
     * @param name       이름 검색
     * @param page       페이지 번호 (0부터 시작)
     * @param size       페이지 크기
     * @return ApiResponse<Page<EmployeeListItemDto>>
     */
    @GetMapping
    public ApiResponse<Page<EmployeeListItemDto>> getEmployeeList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("직원 목록 조회 API 호출 - department: {}, position: {}, name: {}, page: {}, size: {}",
                department, position, name, page, size);

        // 1. 검색 조건 VO 생성
        EmployeeListSearchConditionVo condition = new EmployeeListSearchConditionVo(
                department, position, name
        );

        // 2. 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. Service 호출
        Page<EmployeeListItemDto> result = employeeService.getEmployeeList(condition, pageable);

        log.info("직원 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return ApiResponse.success(result, "직원 목록을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 직원 상세 정보 조회
     *
     * @param employeeId 직원 ID
     * @return ApiResponse<EmployeeDetailDto>
     */
    @GetMapping("/{employeeId}")
    public ApiResponse<EmployeeDetailDto> getEmployeeDetail(@PathVariable String employeeId) {
        log.info("직원 상세 정보 조회 API 호출 - employeeId: {}", employeeId);

        EmployeeDetailDto result = employeeService.getEmployeeDetail(employeeId);

        log.info("직원 상세 정보 조회 성공 - employeeId: {}, employeeName: {}",
                employeeId, result.getName());

        return ApiResponse.success(result, "직원 상세 정보를 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 직원 정보 수정
     *
     * @param employeeId 직원 ID
     * @param requestDto 수정 요청 정보
     * @return ApiResponse<Void>
     */
    @PatchMapping("/{employeeId}")
    public ApiResponse<Void> updateEmployee(
            @PathVariable String employeeId,
            @RequestBody UpdateEmployeeRequestDto requestDto
    ) {
        log.info("직원 정보 수정 API 호출 - employeeId: {}, employeeName: {}",
                employeeId, requestDto.getEmployeeName());

        employeeService.updateEmployee(employeeId, requestDto);

        log.info("직원 정보 수정 성공 - employeeId: {}", employeeId);

        return ApiResponse.success(null, "직원 정보를 수정했습니다.", HttpStatus.OK);
    }

    /**
     * 교육 프로그램 신청
     *
     * @param requestDto 교육 신청 정보 (employeeId, programId 포함)
     * @return ApiResponse<Void>
     */
    @PostMapping("/request")
    public ApiResponse<Void> requestTraining(@RequestBody TrainingRequestDto requestDto) {
        log.info("교육 프로그램 신청 API 호출 - employeeId: {}, programId: {}",
                requestDto.getEmployeeId(), requestDto.getProgramId());

        employeeService.requestTraining(requestDto);

        log.info("교육 프로그램 신청 성공 - employeeId: {}, programId: {}",
                requestDto.getEmployeeId(), requestDto.getProgramId());

        return ApiResponse.success(null, "교육 프로그램 신청이 완료되었습니다.", HttpStatus.OK);
    }
}
