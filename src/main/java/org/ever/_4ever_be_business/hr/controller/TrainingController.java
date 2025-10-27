package org.ever._4ever_be_business.hr.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.hr.dto.request.CreateTrainingProgramDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeTrainingHistoryDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeTrainingListResponseDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeTrainingSummaryDto;
import org.ever._4ever_be_business.hr.dto.response.TrainingListItemDto;
import org.ever._4ever_be_business.hr.dto.response.TrainingResponseDto;
import org.ever._4ever_be_business.hr.dto.response.TrainingStatusResponseDto;
import org.ever._4ever_be_business.hr.enums.TrainingCategory;
import org.ever._4ever_be_business.hr.enums.TrainingStatus;
import org.ever._4ever_be_business.hr.service.TrainingService;
import org.ever._4ever_be_business.hr.vo.EmployeeTrainingHistoryVo;
import org.ever._4ever_be_business.hr.vo.EmployeeTrainingSearchConditionVo;
import org.ever._4ever_be_business.hr.vo.TrainingDetailVo;
import org.ever._4ever_be_business.hr.vo.TrainingSearchConditionVo;
import org.ever._4ever_be_business.hr.vo.TrainingStatusSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@Slf4j
@RestController
@RequestMapping("/hrm")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    /**
     * 교육 프로그램 상세 정보 조회
     *
     * @param programId 교육 프로그램 ID
     * @return DeferredResult<ApiResponse<TrainingResponseDto>>
     */
    @GetMapping("/program/{programId}")
    public DeferredResult<ApiResponse<TrainingResponseDto>> getProgramDetailInfo(@PathVariable String programId) {
        log.info("교육 프로그램 상세 정보 조회 요청 - programId: {}", programId);

        // DeferredResult 생성 (30초 타임아웃)
        DeferredResult<ApiResponse<TrainingResponseDto>> deferredResult =
                new DeferredResult<>(30000L);

        // 타임아웃 처리
        deferredResult.onTimeout(() -> {
            log.warn("교육 프로그램 상세 정보 조회 타임아웃 - programId: {}", programId);
            deferredResult.setResult(ApiResponse.fail("요청 시간이 초과되었습니다.", org.springframework.http.HttpStatus.REQUEST_TIMEOUT));
        });

        // DTO를 VO로 변환
        TrainingDetailVo vo = new TrainingDetailVo(programId);

        // 비동기 Service 호출
        trainingService.getTrainingDetail(vo)
                .thenAccept(response -> {
                    log.info("교육 프로그램 상세 정보 조회 성공 - programId: {}", programId);
                    deferredResult.setResult(
                            ApiResponse.success(response, "교육 프로그램 상세 정보 조회에 성공했습니다.", org.springframework.http.HttpStatus.OK)
                    );
                })
                .exceptionally(throwable -> {
                    log.error("교육 프로그램 상세 정보 조회 실패 - programId: {}", programId, throwable);
                    deferredResult.setResult(
                            ApiResponse.fail("교육 프로그램 상세 정보 조회에 실패했습니다: " + throwable.getMessage(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    );
                    return null;
                });

        return deferredResult;
    }

    /**
     * 교육 프로그램 목록 조회 (검색 + 페이징)
     *
     * @param name     교육 프로그램명 (부분 검색)
     * @param status   교육 상태 (IN_PROGRESS, RECRUITING, COMPLETED)
     * @param category 교육 카테고리
     * @param page     페이지 번호 (0부터 시작)
     * @param size     페이지 크기
     * @return ApiResponse<Page < TrainingListItemDto>>
     */
    @GetMapping("/program")
    public ApiResponse<Page<TrainingListItemDto>> getTrainingList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) TrainingStatus status,
            @RequestParam(required = false) TrainingCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("교육 프로그램 목록 조회 요청 - name: {}, status: {}, category: {}, page: {}, size: {}",
                name, status, category, page, size);

        // 1. 검색 조건 생성
        TrainingSearchConditionVo condition = new TrainingSearchConditionVo(name, status, category);

        // 2. 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. Service 호출
        Page<TrainingListItemDto> result = trainingService.getTrainingList(condition, pageable);

        log.info("교육 프로그램 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return ApiResponse.success(result, "교육 프로그램 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 교육 프로그램 생성
     *
     * @param requestDto 교육 프로그램 생성 정보
     * @return ApiResponse<Void>
     */
    @PostMapping("/program")
    public ApiResponse<Void> createTrainingProgram(@RequestBody CreateTrainingProgramDto requestDto) {
        log.info("교육 프로그램 생성 API 호출 - programName: {}, category: {}",
                requestDto.getProgramName(), requestDto.getCategory());

        trainingService.createTrainingProgram(requestDto);

        log.info("교육 프로그램 생성 성공 - programName: {}", requestDto.getProgramName());

        return ApiResponse.success(null, "교육 프로그램이 생성되었습니다.", HttpStatus.CREATED);
    }

    /**
     * 직원 교육 이력 조회
     *
     * @param employeeId Employee ID
     * @return ApiResponse<EmployeeTrainingHistoryDto>
     */
    @GetMapping("/employee/{employeeId}/training-history")
    public ApiResponse<EmployeeTrainingHistoryDto> getEmployeeTrainingHistory(
            @PathVariable String employeeId
    ) {
        log.info("직원 교육 이력 조회 요청 - employeeId: {}", employeeId);

        // PathVariable을 VO로 변환
        EmployeeTrainingHistoryVo vo = new EmployeeTrainingHistoryVo(employeeId);

        // Service 호출
        EmployeeTrainingHistoryDto result = trainingService.getEmployeeTrainingHistory(vo);

        log.info("직원 교육 이력 조회 성공 - employeeId: {}, completedCount: {}, requiredMissingCount: {}",
                employeeId, result.getCompletedCount(), result.getRequiredMissingCount());

        return ApiResponse.success(result, "직원 교육 이력 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 직원 교육 현황 목록 조회 (검색 + 페이징)
     *
     * @param department 부서명 (선택)
     * @param position   직급명 (선택)
     * @param name       직원명 (선택, 부분 검색)
     * @param page       페이지 번호 (0부터 시작)
     * @param size       페이지 크기
     * @return ApiResponse<EmployeeTrainingListResponseDto>
     */
    @GetMapping
    public ApiResponse<EmployeeTrainingListResponseDto> getEmployeeTrainingList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("직원 교육 현황 목록 조회 요청 - department: {}, position: {}, name: {}, page: {}, size: {}",
                department, position, name, page, size);

        // 1. 검색 조건 생성
        EmployeeTrainingSearchConditionVo condition = new EmployeeTrainingSearchConditionVo(department, position, name);

        // 2. 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. Service 호출
        EmployeeTrainingListResponseDto result = trainingService.getEmployeeTrainingList(condition, pageable);

        log.info("직원 교육 현황 목록 조회 성공 - totalElements: {}",
                result.getPage().getTotalElements());

        return ApiResponse.success(result, "목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 직원 교육 현황 통계 조회 (검색 + 페이징)
     *
     * @param department 부서 ID (선택)
     * @param position   직급 ID (선택)
     * @param name       직원명 (선택)
     * @param page       페이지 번호 (0부터 시작)
     * @param size       페이지 크기
     * @return ApiResponse<TrainingStatusResponseDto>
     */
    @GetMapping("/training-status")
    public ApiResponse<TrainingStatusResponseDto> getTrainingStatusList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("직원 교육 현황 통계 조회 API 호출 - department: {}, position: {}, name: {}, page: {}, size: {}",
                department, position, name, page, size);

        // 1. 검색 조건 생성
        TrainingStatusSearchConditionVo condition = new TrainingStatusSearchConditionVo(department, position, name);

        // 2. 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. Service 호출
        TrainingStatusResponseDto result = trainingService.getTrainingStatusList(condition, pageable);

        log.info("직원 교육 현황 통계 조회 성공 - totalElements: {}",
                result.getPage().getTotalElements());

        return ApiResponse.success(result, "목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 직원별 교육 요약 정보 조회
     *
     * @param employeeId 직원 ID
     * @return ApiResponse<EmployeeTrainingSummaryDto>
     */
    @GetMapping("/training/employee/{employeeId}")
    public ApiResponse<EmployeeTrainingSummaryDto> getEmployeeTrainingSummary(
            @PathVariable String employeeId
    ) {
        log.info("직원별 교육 요약 정보 조회 API 호출 - employeeId: {}", employeeId);

        EmployeeTrainingSummaryDto result = trainingService.getEmployeeTrainingSummary(employeeId);

        log.info("직원별 교육 요약 정보 조회 성공 - employeeId: {}, employeeName: {}",
                employeeId, result.getEmployeeName());

        return ApiResponse.success(result, "직원 교육 이력 조회에 성공했습니다.", HttpStatus.OK);
    }
}
