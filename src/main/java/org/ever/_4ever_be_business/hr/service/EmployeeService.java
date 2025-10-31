package org.ever._4ever_be_business.hr.service;

import jakarta.transaction.Transactional;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.hr.dto.request.EmployeeCreateRequestDto;
import org.ever._4ever_be_business.hr.dto.request.TrainingRequestDto;
import org.ever._4ever_be_business.hr.dto.request.UpdateEmployeeRequestDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeDetailDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeListItemDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeWithTrainingDto;
import org.ever._4ever_be_business.hr.dto.response.TrainingProgramSimpleDto;
import org.ever._4ever_be_business.hr.vo.EmployeeListSearchConditionVo;
import org.ever.event.CreateAuthUserResultEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

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

    /**
     * InternelUser ID로 직원 정보 및 교육 이력 조회
     *
     * @param internelUserId InternelUser ID
     * @return 직원 정보 및 교육 이력
     */
    EmployeeWithTrainingDto getEmployeeWithTrainingByInternelUserId(String internelUserId);

    /**
     * InternelUser ID로 수강 가능한 교육 프로그램 목록 조회
     * (수강 중이지 않고, 모집 중이 아닌 교육 프로그램)
     *
     * @param internelUserId InternelUser ID
     * @return 수강 가능한 교육 프로그램 목록
     */
    List<TrainingProgramSimpleDto> getAvailableTrainingsByInternelUserId(String internelUserId);

    /**
     * 내부 사용자 생성
     *
     * @param requestDto 직원 생성 요청 정보
     * @param deferredResult 비동기 결과
     */
    @Transactional
    void createEmployee(
            EmployeeCreateRequestDto requestDto,
            DeferredResult<ResponseEntity<ApiResponse<CreateAuthUserResultEvent>>> deferredResult
    );
}
