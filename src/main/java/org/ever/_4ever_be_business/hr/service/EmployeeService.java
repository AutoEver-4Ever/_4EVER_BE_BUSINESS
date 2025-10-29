package org.ever._4ever_be_business.hr.service;

import jakarta.transaction.Transactional;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.hr.dto.request.EmployeeCreateRequestDto;
import org.ever._4ever_be_business.hr.dto.request.TrainingRequestDto;
import org.ever._4ever_be_business.hr.dto.request.UpdateEmployeeRequestDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeDetailDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeListItemDto;
import org.ever._4ever_be_business.hr.vo.EmployeeListSearchConditionVo;
import org.ever.event.CreateAuthUserResultEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

public interface EmployeeService {
    /**
     * 직원 상세 정보 조회
     *
     * @param employeeId 직원 ID
     * @return 직원 상세 정보
     */
    EmployeeDetailDto getEmployeeDetail(String employeeId);

    /**
     * 직원 목록 조회
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<EmployeeListItemDto>
     */
    Page<EmployeeListItemDto> getEmployeeList(EmployeeListSearchConditionVo condition, Pageable pageable);

    /**
     * 직원 정보 수정
     *
     * @param employeeId 직원 ID
     * @param requestDto 수정 요청 정보
     */
    void updateEmployee(String employeeId, UpdateEmployeeRequestDto requestDto);

    /**
     * 교육 프로그램 신청
     *
     * @param requestDto 교육 신청 정보 (employeeId, programId 포함)
     */
    void requestTraining(TrainingRequestDto requestDto);

    // 내부 사용자 생성

    @Transactional
    void createEmployee(
            EmployeeCreateRequestDto requestDto,
            DeferredResult<ResponseEntity<ApiResponse<CreateAuthUserResultEvent>>> deferredResult
    );
}
