package org.ever._4ever_be_business.hr.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.hr.dto.response.PayrollListItemDto;
import org.ever._4ever_be_business.hr.dto.response.PaystubDetailDto;
import org.ever._4ever_be_business.hr.service.PayrollService;
import org.ever._4ever_be_business.hr.vo.PayrollSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/hrm/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    /**
     * 급여 명세서 상세 조회
     *
     * @param payrollId 급여 명세서 ID
     * @return ApiResponse<PaystubDetailDto>
     */
    @GetMapping("/{payrollId}")
    public ApiResponse<PaystubDetailDto> getPaystubDetail(@PathVariable String payrollId) {
        log.info("급여 명세서 상세 조회 API 호출 - payrollId: {}", payrollId);

        PaystubDetailDto result = payrollService.getPaystubDetail(payrollId);

        log.info("급여 명세서 상세 조회 성공 - payrollId: {}, employeeName: {}",
                payrollId, result.getEmployee().getEmployeeName());

        return ApiResponse.success(result, "급여 명세서 상세 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 급여 명세서 목록 조회 (검색 + 페이징)
     *
     * @param year       년도
     * @param month      월
     * @param name       이름
     * @param department 부서 ID
     * @param position   직급 ID
     * @param page       페이지 번호 (0부터 시작)
     * @param size       페이지 크기
     * @return ApiResponse<Page<PayrollListItemDto>>
     */
    @GetMapping
    public ApiResponse<Page<PayrollListItemDto>> getPayrollList(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("급여 명세서 목록 조회 API 호출 - year: {}, month: {}, name: {}, department: {}, position: {}, page: {}, size: {}",
                year, month, name, department, position, page, size);

        // 1. 검색 조건 VO 생성
        PayrollSearchConditionVo condition = new PayrollSearchConditionVo(year, month, name, department, position);

        // 2. 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. Service 호출
        Page<PayrollListItemDto> result = payrollService.getPayrollList(condition, pageable);

        log.info("급여 명세서 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return ApiResponse.success(result, "급여 명세서 목록 조회에 성공했습니다.", HttpStatus.OK);
    }
}
