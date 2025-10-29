package org.ever._4ever_be_business.hr.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.dto.request.*;
import org.ever._4ever_be_business.hr.dto.response.*;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.hr.entity.Department;
import org.ever._4ever_be_business.hr.entity.InternelUser;
import org.ever._4ever_be_business.hr.entity.Position;
import org.ever._4ever_be_business.hr.enums.LeaveType;
import org.ever._4ever_be_business.hr.enums.PayrollStatus;
import org.ever._4ever_be_business.hr.enums.TrainingCategory;
import org.ever._4ever_be_business.hr.enums.TrainingStatus;
import org.ever._4ever_be_business.tam.enums.AttendanceStatus;
import org.ever._4ever_be_business.hr.repository.CustomerUserRepository;
import org.ever._4ever_be_business.hr.repository.DepartmentRepository;
import org.ever._4ever_be_business.hr.repository.InternelUserRepository;
import org.ever._4ever_be_business.hr.repository.PositionRepository;
import org.ever._4ever_be_business.hr.repository.TrainingRepository;
import org.ever._4ever_be_business.hr.service.*;
import org.ever._4ever_be_business.hr.vo.*;
import org.ever._4ever_be_business.sd.dto.response.PageInfo;
import org.ever.event.CreateAuthUserResultEvent;
import org.ever._4ever_be_business.tam.dto.request.CheckInRequestDto;
import org.ever._4ever_be_business.tam.dto.request.CheckOutRequestDto;
import org.ever._4ever_be_business.tam.dto.request.UpdateTimeRecordDto;
import org.ever._4ever_be_business.tam.dto.response.AttendanceListItemDto;
import org.ever._4ever_be_business.tam.dto.response.AttendanceRecordDto;
import org.ever._4ever_be_business.tam.dto.response.AttendanceStatusDto;
import org.ever._4ever_be_business.tam.dto.response.TimeRecordDetailDto;
import org.ever._4ever_be_business.tam.dto.response.TimeRecordListItemDto;
import org.ever._4ever_be_business.tam.service.AttendanceService;
import org.ever._4ever_be_business.tam.service.TimeRecordService;
import org.ever._4ever_be_business.tam.vo.AttendanceListSearchConditionVo;
import org.ever._4ever_be_business.tam.vo.AttendanceSearchConditionVo;
import org.ever._4ever_be_business.tam.vo.TimeRecordDetailVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/hrm")
@RequiredArgsConstructor
public class HrmController {

    private final DepartmentService departmentService;
    private final PositionService positionService;
    private final EmployeeService employeeService;
    private final LeaveRequestService leaveRequestService;
    private final PayrollService payrollService;
    private final StatisticsService statisticsService;
    private final TrainingService trainingService;
    private final TimeRecordService timeRecordService;
    private final AttendanceService attendanceService;
    private final CustomerUserService customerUserService;
    private final InternelUserRepository internelUserRepository;
    private final CustomerUserRepository customerUserRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final TrainingRepository trainingRepository;

    // ==================== Statistics ====================

    /**
     * HR 대시보드 통계 조회
     */
    @GetMapping("/statistics")
    public ApiResponse<HRStatisticsResponseDto> getHRStatistics() {
        log.info("HR 대시보드 통계 조회 API 호출");
        HRStatisticsResponseDto result = statisticsService.getHRStatistics();
        log.info("HR 대시보드 통계 조회 성공");
        return ApiResponse.success(result, "대시보드 정보를 성공적으로 조회했습니다.", HttpStatus.OK);
    }

    // ==================== Departments ====================

    /**
     * 부서 목록 조회
     */
    @GetMapping("/departments")
    public ApiResponse<DepartmentListResponseDto> getDepartmentList(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("부서 목록 조회 API 호출 - status: {}, page: {}, size: {}", status, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<DepartmentListItemDto> pageResult = departmentService.getDepartmentList(status, pageable);

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

        log.info("부서 목록 조회 성공 - total: {}, size: {}", pageResult.getTotalElements(), pageResult.getContent().size());
        return ApiResponse.success(responseDto, "부서 목록을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 부서 상세 정보 조회
     */
    @GetMapping("/organization/department/{departmentId}")
    public ApiResponse<DepartmentDetailDto> getDepartmentDetail(@PathVariable String departmentId) {
        log.info("부서 상세 정보 조회 API 호출 - departmentId: {}", departmentId);
        DepartmentDetailDto result = departmentService.getDepartmentDetail(departmentId);
        log.info("부서 상세 정보 조회 성공 - departmentId: {}, departmentName: {}, headcount: {}",
                departmentId, result.getDepartmentName(), result.getHeadcount());
        return ApiResponse.success(result, "부서 상세 정보 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 전체 부서 목록 조회 (ID, Name만)
     */
    @GetMapping("/departments/simple")
    public ApiResponse<List<DepartmentSimpleDto>> getAllDepartments() {
        log.info("전체 부서 목록 조회 API 호출");

        List<DepartmentSimpleDto> result = departmentRepository.findAll().stream()
                .map(dept -> new DepartmentSimpleDto(dept.getId(), dept.getDepartmentName()))
                .collect(java.util.stream.Collectors.toList());

        log.info("전체 부서 목록 조회 성공 - count: {}", result.size());
        return ApiResponse.success(result, "부서 목록을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 부서 구성원 목록 조회 (ID, Name만)
     */
    @GetMapping("/departments/{departmentId}/members")
    public ApiResponse<List<DepartmentMemberDto>> getDepartmentMembers(@PathVariable String departmentId) {
        log.info("부서 구성원 목록 조회 API 호출 - departmentId: {}", departmentId);

        List<DepartmentMemberDto> result = departmentService.getDepartmentMembers(departmentId);

        log.info("부서 구성원 목록 조회 성공 - count: {}", result.size());
        return ApiResponse.success(result, "부서 구성원 목록을 조회했습니다.", HttpStatus.OK);
    }

    // ==================== Positions ====================

    /**
     * 직급 목록 조회
     */
    @GetMapping("/positions")
    public ApiResponse<List<PositionListItemDto>> getPositionList() {
        log.info("직급 목록 조회 API 호출");
        List<PositionListItemDto> result = positionService.getPositionList();
        log.info("직급 목록 조회 성공 - count: {}", result.size());
        return ApiResponse.success(result, "직급 목록을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 직급 상세 정보 조회
     */
    @GetMapping("/organization/position/{positionId}")
    public ApiResponse<PositionDetailDto> getPositionDetail(@PathVariable String positionId) {
        log.info("직급 상세 정보 조회 API 호출 - positionId: {}", positionId);
        PositionDetailDto result = positionService.getPositionDetail(positionId);
        log.info("직급 상세 정보 조회 성공 - positionId: {}, positionName: {}, headCount: {}",
                positionId, result.getPositionName(), result.getHeadCount());
        return ApiResponse.success(result, "직급 상세 정보를 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 전체 직급 목록 조회 (ID, Name만)
     */
    @GetMapping("/positions/simple")
    public ApiResponse<List<PositionSimpleDto>> getAllPositions() {
        log.info("전체 직급 목록 조회 API 호출");

        List<PositionSimpleDto> result = positionRepository.findAll().stream()
                .map(pos -> new PositionSimpleDto(pos.getId(), pos.getPositionName()))
                .collect(java.util.stream.Collectors.toList());

        log.info("전체 직급 목록 조회 성공 - count: {}", result.size());
        return ApiResponse.success(result, "직급 목록을 조회했습니다.", HttpStatus.OK);
    }

    // ==================== Employees ====================

    /**
     * 직원 목록 조회
     */
    @GetMapping("/employee")
    public ApiResponse<Page<EmployeeListItemDto>> getEmployeeList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("직원 목록 조회 API 호출 - department: {}, position: {}, name: {}, page: {}, size: {}",
                department, position, name, page, size);

        EmployeeListSearchConditionVo condition = new EmployeeListSearchConditionVo(department, position, name);
        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeListItemDto> result = employeeService.getEmployeeList(condition, pageable);

        log.info("직원 목록 조회 성공 - totalElements: {}, totalPages: {}", result.getTotalElements(), result.getTotalPages());
        return ApiResponse.success(result, "직원 목록을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 직원 상세 정보 조회
     */
    @GetMapping("/employee/{employeeId}")
    public ApiResponse<EmployeeDetailDto> getEmployeeDetail(@PathVariable String employeeId) {
        log.info("직원 상세 정보 조회 API 호출 - employeeId: {}", employeeId);
        EmployeeDetailDto result = employeeService.getEmployeeDetail(employeeId);
        log.info("직원 상세 정보 조회 성공 - employeeId: {}, employeeName: {}", employeeId, result.getName());
        return ApiResponse.success(result, "직원 상세 정보를 조회했습니다.", HttpStatus.OK);
    }

    /**
     * InternelUser ID로 직원 정보 및 교육 이력 조회
     */
    @GetMapping("/employees/{internelUserId}")
    public ApiResponse<EmployeeWithTrainingDto> getEmployeeWithTrainingByInternelUserId(@PathVariable String internelUserId) {
        log.info("InternelUser ID로 직원 정보 및 교육 이력 조회 API 호출 - internelUserId: {}", internelUserId);
        EmployeeWithTrainingDto result = employeeService.getEmployeeWithTrainingByInternelUserId(internelUserId);
        log.info("InternelUser ID로 직원 정보 및 교육 이력 조회 성공 - internelUserId: {}, employeeName: {}, trainingCount: {}",
                internelUserId, result.getName(), result.getTrainings().size());
        return ApiResponse.success(result, "직원 정보 및 교육 이력을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * InternelUser ID로 수강 가능한 교육 프로그램 목록 조회
     * (수강 중이지 않고, 모집 중이 아닌 교육 프로그램)
     */
    @GetMapping("/employees/{internelUserId}/available-trainings")
    public ApiResponse<List<TrainingProgramSimpleDto>> getAvailableTrainingsByInternelUserId(@PathVariable String internelUserId) {
        log.info("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 API 호출 - internelUserId: {}", internelUserId);
        List<TrainingProgramSimpleDto> result = employeeService.getAvailableTrainingsByInternelUserId(internelUserId);
        log.info("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 성공 - internelUserId: {}, availableCount: {}",
                internelUserId, result.size());
        return ApiResponse.success(result, "수강 가능한 교육 프로그램 목록을 조회했습니다.", HttpStatus.OK);
    }

    // ==================== 사용자 이름 조회 ====================

    /**
     * 내부 사용자 이름 단건 조회
     */
    @GetMapping("/users/internal/{userId}")
    public ApiResponse<UserNameResponse> getInternalUserName(@PathVariable String userId) {
        log.info("내부 사용자 이름 조회 API 호출 - userId: {}", userId);

        InternelUser internelUser = internelUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "내부 사용자 정보를 찾을 수 없습니다."));

        UserNameResponse response = new UserNameResponse(internelUser.getUserId(), internelUser.getName());
        log.info("내부 사용자 이름 조회 성공 - userId: {}, userName: {}", response.getUserId(), response.getUserName());
        return ApiResponse.success(response, "내부 사용자 이름을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 고객 사용자 이름 단건 조회
     */
    @GetMapping("/users/customer/{userId}")
    public ApiResponse<UserNameResponse> getCustomerUserName(@PathVariable String userId) {
        log.info("고객 사용자 이름 조회 API 호출 - userId: {}", userId);

        CustomerUser customerUser = customerUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "고객 사용자 정보를 찾을 수 없습니다."));

        UserNameResponse response = new UserNameResponse(customerUser.getUserId(), customerUser.getCustomerName());
        log.info("고객 사용자 이름 조회 성공 - userId: {}, userName: {}", response.getUserId(), response.getUserName());
        return ApiResponse.success(response, "고객 사용자 이름을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * CustomerUser ID로 고객 사용자 상세 정보 조회
     */
    @GetMapping("/customers/by-customer-user/{customerUserId}")
    public ApiResponse<CustomerUserDetailDto> getCustomerUserDetailByUserId(@PathVariable String customerUserId) {
        log.info("CustomerUser ID로 고객 사용자 상세 정보 조회 API 호출 - customerUserId: {}", customerUserId);
        CustomerUserDetailDto result = customerUserService.getCustomerUserDetailByUserId(customerUserId);
        log.info("CustomerUser ID로 고객 사용자 상세 정보 조회 성공 - customerUserId: {}, customerName: {}",
                customerUserId, result.getCustomerName());
        return ApiResponse.success(result, "고객 사용자 상세 정보를 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 직원 정보 수정
     */
    @PatchMapping("/employee/{employeeId}")
    public ApiResponse<Void> updateEmployee(
            @PathVariable String employeeId,
            @RequestBody UpdateEmployeeRequestDto requestDto) {
        log.info("직원 정보 수정 API 호출 - employeeId: {}, employeeName: {}", employeeId, requestDto.getEmployeeName());
        employeeService.updateEmployee(employeeId, requestDto);
        log.info("직원 정보 수정 성공 - employeeId: {}", employeeId);
        return ApiResponse.success(null, "직원 정보를 수정했습니다.", HttpStatus.OK);
    }

    /**
     * 교육 프로그램 신청
     */
    @PostMapping("/employee/request")
    public ApiResponse<Void> requestTraining(@RequestBody TrainingRequestDto requestDto) {
        log.info("교육 프로그램 신청 API 호출 - employeeId: {}, programId: {}", requestDto.getEmployeeId(), requestDto.getProgramId());
        employeeService.requestTraining(requestDto);
        log.info("교육 프로그램 신청 성공 - employeeId: {}, programId: {}", requestDto.getEmployeeId(), requestDto.getProgramId());
        return ApiResponse.success(null, "교육 프로그램 신청이 완료되었습니다.", HttpStatus.OK);
    }

    /**
     * 직원 교육 프로그램 등록
     */
    @PostMapping("/program/{employeeId}")
    public ApiResponse<Void> enrollTrainingProgram(
            @PathVariable String employeeId,
            @RequestBody TrainingRequestDto requestDto) {
        log.info("교육 프로그램 등록 API 호출 - employeeId: {}, programId: {}", employeeId, requestDto.getProgramId());
        requestDto.setEmployeeId(employeeId);
        employeeService.requestTraining(requestDto);
        log.info("교육 프로그램 등록 성공 - employeeId: {}, programId: {}", employeeId, requestDto.getProgramId());
        return ApiResponse.success(null, "교육 프로그램 등록이 완료되었습니다.", HttpStatus.CREATED);
    }

    // ==================== Leave Requests ====================

    /**
     * 휴가 신청 목록 조회
     */
    @GetMapping("/leave/request")
    public ApiResponse<Page<LeaveRequestListItemDto>> getLeaveRequestList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LeaveType type,
            @RequestParam(required = false, defaultValue = "DESC") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("휴가 신청 목록 조회 API 호출 - department: {}, position: {}, name: {}, type: {}, sortOrder: {}, page: {}, size: {}",
                department, position, name, type, sortOrder, page, size);

        LeaveRequestSearchConditionVo condition = new LeaveRequestSearchConditionVo(department, position, name, type, sortOrder);
        Pageable pageable = PageRequest.of(page, size);
        Page<LeaveRequestListItemDto> result = leaveRequestService.getLeaveRequestList(condition, pageable);

        log.info("휴가 신청 목록 조회 성공 - totalElements: {}, totalPages: {}", result.getTotalElements(), result.getTotalPages());
        return ApiResponse.success(result, "휴가 신청 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 휴가 신청
     */
    @PostMapping("/leave/request")
    public ApiResponse<Void> createLeaveRequest(@RequestBody CreateLeaveRequestDto requestDto) {
        log.info("휴가 신청 API 호출 - employeeId: {}, leaveType: {}, startDate: {}, endDate: {}",
                requestDto.getEmployeeId(), requestDto.getLeaveType(), requestDto.getStartDate(), requestDto.getEndDate());
        leaveRequestService.createLeaveRequest(requestDto);
        log.info("휴가 신청 성공 - employeeId: {}", requestDto.getEmployeeId());
        return ApiResponse.success(null, "휴가 신청이 완료되었습니다.", HttpStatus.OK);
    }

    /**
     * 휴가 신청 승인
     */
    @PatchMapping("/leave/request/{requestId}/release")
    public ApiResponse<Void> approveLeaveRequest(@PathVariable String requestId) {
        log.info("휴가 신청 승인 API 호출 - requestId: {}", requestId);
        leaveRequestService.approveLeaveRequest(requestId);
        log.info("휴가 신청 승인 성공 - requestId: {}", requestId);
        return ApiResponse.success(null, "휴가 신청이 승인되었습니다.", HttpStatus.OK);
    }

    /**
     * 휴가 신청 반려
     */
    @PatchMapping("/leave/request/{requestId}/reject")
    public ApiResponse<Void> rejectLeaveRequest(@PathVariable String requestId) {
        log.info("휴가 신청 반려 API 호출 - requestId: {}", requestId);
        leaveRequestService.rejectLeaveRequest(requestId);
        log.info("휴가 신청 반려 성공 - requestId: {}", requestId);
        return ApiResponse.success(null, "휴가 신청이 반려되었습니다.", HttpStatus.OK);
    }

    /**
     * 잔여 연차 조회
     */
    @GetMapping("/leave-request")
    public ApiResponse<RemainingLeaveDaysDto> getRemainingLeaveDays(@RequestParam String userId) {
        log.info("잔여 연차 조회 API 호출 - userId: {}", userId);
        RemainingLeaveDaysDto result = leaveRequestService.getRemainingLeaveDays(userId);
        log.info("잔여 연차 조회 성공 - userId: {}, remainingLeaveDays: {}", userId, result.getRemainingLeaveDays());
        return ApiResponse.success(result, "잔여 연차를 조회했습니다.", HttpStatus.OK);
    }

    // ==================== Payroll ====================

    /**
     * 급여 명세서 상세 조회
     */
    @GetMapping("/payroll/{payrollId}")
    public ApiResponse<PaystubDetailDto> getPaystubDetail(@PathVariable String payrollId) {
        log.info("급여 명세서 상세 조회 API 호출 - payrollId: {}", payrollId);
        PaystubDetailDto result = payrollService.getPaystubDetail(payrollId);
        log.info("급여 명세서 상세 조회 성공 - payrollId: {}, employeeName: {}", payrollId, result.getEmployee().getEmployeeName());
        return ApiResponse.success(result, "급여 명세서 상세 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 급여 명세서 목록 조회
     */
    @GetMapping("/payroll")
    public ApiResponse<Page<PayrollListItemDto>> getPayrollList(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("급여 명세서 목록 조회 API 호출 - year: {}, month: {}, name: {}, department: {}, position: {}, page: {}, size: {}",
                year, month, name, department, position, page, size);

        PayrollSearchConditionVo condition = new PayrollSearchConditionVo(year, month, name, department, position);
        Pageable pageable = PageRequest.of(page, size);
        Page<PayrollListItemDto> result = payrollService.getPayrollList(condition, pageable);

        log.info("급여 명세서 목록 조회 성공 - totalElements: {}, totalPages: {}", result.getTotalElements(), result.getTotalPages());
        return ApiResponse.success(result, "급여 명세서 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 급여 지급 완료 처리
     */
    @PostMapping("/payroll/complete")
    public ApiResponse<Void> completePayroll(@RequestBody CompletePayrollRequestDto requestDto) {
        log.info("급여 지급 완료 처리 API 호출 - payrollId: {}", requestDto.getPayrollId());
        payrollService.completePayroll(requestDto.getPayrollId());
        log.info("급여 지급 완료 처리 성공 - payrollId: {}", requestDto.getPayrollId());
        return ApiResponse.success(null, "급여 지급이 완료되었습니다.", HttpStatus.OK);
    }

    /**
     * 모든 직원 당월 급여 생성
     */
    @PostMapping("/payroll/generate")
    public ApiResponse<Void> generateMonthlyPayroll() {
        log.info("모든 직원 당월 급여 생성 API 호출");
        payrollService.generateMonthlyPayrollForAllEmployees();
        log.info("모든 직원 당월 급여 생성 완료");
        return ApiResponse.success(null, "급여가 생성되었습니다.", HttpStatus.CREATED);
    }

    /**
     * 급여 상태 목록 조회 (enum 전체)
     */
    @GetMapping("/payroll/statuses")
    public ApiResponse<List<PayrollStatusDto>> getAllPayrollStatuses() {
        log.info("급여 상태 목록 조회 API 호출");

        List<PayrollStatusDto> result = java.util.Arrays.stream(PayrollStatus.values())
                .map(status -> new PayrollStatusDto(
                        status.name(),
                        getPayrollStatusDescription(status)
                ))
                .collect(java.util.stream.Collectors.toList());

        log.info("급여 상태 목록 조회 성공 - count: {}", result.size());
        return ApiResponse.success(result, "급여 상태 목록을 조회했습니다.", HttpStatus.OK);
    }

    private String getPayrollStatusDescription(PayrollStatus status) {
        return switch (status) {
            case DRAFT -> "초안(집계 전)";
            case PENDING_APPROVAL -> "승인 대기";
            case APPROVED -> "승인 완료";
            case CALCULATING -> "급여 계산 중";
            case CALCULATED -> "계산 완료(전표/지급 전 검증 단계)";
            case PENDING_PAYMENT -> "지급 지시 대기(이체 파일/펌뱅킹 전)";
            case PAID -> "지급 완료(은행 이체/현금 지급 완료)";
            case PARTIALLY_PAID -> "일부 지급";
            case POSTING -> "회계 반영 중";
            case POSTED -> "회계 반영 완료(전표 확정)";
            case ON_HOLD -> "보류(이의제기/감사)";
            case ADJUSTING -> "정정 처리 중(추가공제/보너스/소급)";
            case ADJUSTED -> "정정 반영 완료";
            case FAILED -> "계산/지급/전표 어느 단계에서든 실패";
            case CANCELLED -> "승인 전 취소";
            case REVERSED -> "지급/전표 이후 취소(역분개/반제)";
            case CLOSED -> "마감(추가 변경 불가)";
        };
    }

    // ==================== Training ====================

    /**
     * 교육 프로그램 상세 정보 조회
     */
    @GetMapping("/trainings/program/{programId}")
    public DeferredResult<ApiResponse<TrainingResponseDto>> getProgramDetailInfo(@PathVariable String programId) {
        log.info("교육 프로그램 상세 정보 조회 요청 - programId: {}", programId);

        DeferredResult<ApiResponse<TrainingResponseDto>> deferredResult = new DeferredResult<>(30000L);

        deferredResult.onTimeout(() -> {
            log.warn("교육 프로그램 상세 정보 조회 타임아웃 - programId: {}", programId);
            deferredResult.setResult(ApiResponse.fail("요청 시간이 초과되었습니다.", HttpStatus.REQUEST_TIMEOUT));
        });

        TrainingDetailVo vo = new TrainingDetailVo(programId);

        trainingService.getTrainingDetail(vo)
                .thenAccept(response -> {
                    log.info("교육 프로그램 상세 정보 조회 성공 - programId: {}", programId);
                    deferredResult.setResult(ApiResponse.success(response, "교육 프로그램 상세 정보 조회에 성공했습니다.", HttpStatus.OK));
                })
                .exceptionally(throwable -> {
                    log.error("교육 프로그램 상세 정보 조회 실패 - programId: {}", programId, throwable);
                    deferredResult.setResult(ApiResponse.fail("교육 프로그램 상세 정보 조회에 실패했습니다: " + throwable.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
                    return null;
                });

        return deferredResult;
    }

    /**
     * 교육 프로그램 목록 조회
     */
    @GetMapping("/trainings/program")
    public ApiResponse<Page<TrainingListItemDto>> getTrainingList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) TrainingStatus status,
            @RequestParam(required = false) TrainingCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("교육 프로그램 목록 조회 요청 - name: {}, status: {}, category: {}, page: {}, size: {}",
                name, status, category, page, size);

        TrainingSearchConditionVo condition = new TrainingSearchConditionVo(name, status, category);
        Pageable pageable = PageRequest.of(page, size);
        Page<TrainingListItemDto> result = trainingService.getTrainingList(condition, pageable);

        log.info("교육 프로그램 목록 조회 성공 - totalElements: {}, totalPages: {}", result.getTotalElements(), result.getTotalPages());
        return ApiResponse.success(result, "교육 프로그램 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 교육 프로그램 생성
     */
    @PostMapping("/trainings/program")
    public ApiResponse<Void> createTrainingProgram(@RequestBody CreateTrainingProgramDto requestDto) {
        log.info("교육 프로그램 생성 API 호출 - programName: {}, category: {}", requestDto.getProgramName(), requestDto.getCategory());
        trainingService.createTrainingProgram(requestDto);
        log.info("교육 프로그램 생성 성공 - programName: {}", requestDto.getProgramName());
        return ApiResponse.success(null, "교육 프로그램이 생성되었습니다.", HttpStatus.CREATED);
    }

    /**
     * 교육 프로그램 수정
     */
    @PatchMapping("/program/{programId}")
    public ApiResponse<Void> updateTrainingProgram(
            @PathVariable String programId,
            @RequestBody UpdateTrainingProgramDto requestDto) {
        log.info("교육 프로그램 수정 API 호출 - programId: {}, programName: {}, statusCode: {}",
                programId, requestDto.getProgramName(), requestDto.getStatusCode());
        trainingService.updateTrainingProgram(programId, requestDto);
        log.info("교육 프로그램 수정 성공 - programId: {}", programId);
        return ApiResponse.success(null, "교육 프로그램이 수정되었습니다.", HttpStatus.OK);
    }

    /**
     * 교육 카테고리 목록 조회 (enum 전체)
     */
    @GetMapping("/trainings/categories")
    public ApiResponse<List<TrainingCategoryDto>> getAllTrainingCategories() {
        log.info("교육 카테고리 목록 조회 API 호출");

        List<TrainingCategoryDto> result = java.util.Arrays.stream(TrainingCategory.values())
                .map(category -> new TrainingCategoryDto(
                        category.name(),
                        getTrainingCategoryDescription(category)
                ))
                .collect(java.util.stream.Collectors.toList());

        log.info("교육 카테고리 목록 조회 성공 - count: {}", result.size());
        return ApiResponse.success(result, "교육 카테고리 목록을 조회했습니다.", HttpStatus.OK);
    }

    private String getTrainingCategoryDescription(TrainingCategory category) {
        return switch (category) {
            case BASIC_TRAINING -> "기본 교육";
            case TECHNICAL_TRAINING -> "기술 교육";
            case SOFT_SKILL_TRAINING -> "소프트 스킬 교육";
            case MARKETING_TRAINING -> "마케팅 교육";
            case INTERNSHIP -> "인턴십: 정식 채용 전 실무 경험 중심의 프로그램";
            case ONBOARDING -> "온보딩: 신입/이직자 대상 회사 적응 및 기본교육";
            case TRAINING -> "일반 직무교육: 업무 역량 강화를 위한 사내/외 교육";
            case WORKSHOP -> "워크숍: 팀 단위 문제 해결, 협업, 조직문화 중심의 단기 프로그램";
            case SEMINAR -> "세미나: 특정 주제에 대한 단기 발표/토론 세션";
            case COURSE -> "코스형 교육: 정규 과정 형태의 교육";
            case CERTIFICATION -> "자격 취득 교육: 자격증 준비나 인증 관련 교육";
            case COMPLIANCE -> "준법/윤리 교육: 법정의무교육, 개인정보보호, 성희롱 방지 등";
            case SAFETY -> "안전교육: 산업안전, 보건, 소방 등 필수 안전 관련 교육";
            case LEADERSHIP -> "리더십 교육: 관리자, 팀장 대상 리더십/코칭 스킬 강화";
            case TECHNICAL -> "기술교육: IT/개발/엔지니어링 등 전문 기술 중심";
            case SOFT_SKILL -> "소프트스킬: 커뮤니케이션, 협업, 문제해결 등 비기술 역량";
            case LANGUAGE -> "어학 교육: 영어, 일본어 등 언어 역량 향상";
            case SALES -> "영업/CS 관련 교육";
            case MANAGEMENT -> "경영, 전략, 재무 등 관리직 중심 교육";
            case EXTERNAL -> "외부 위탁 교육: 외부 기관/대학/온라인 플랫폼 교육";
            case MANDATORY -> "법정의무교육: 정부/기관에서 의무화된 교육";
            case MENTORING -> "멘토링/코칭 프로그램";
            case OTHER -> "기타 교육";
        };
    }

    /**
     * 전체 교육 프로그램 목록 조회 (ID, Name만)
     */
    @GetMapping("/trainings/programs")
    public ApiResponse<List<TrainingProgramSimpleDto>> getAllTrainingPrograms() {
        log.info("전체 교육 프로그램 목록 조회 API 호출");

        List<TrainingProgramSimpleDto> result = trainingRepository.findAll().stream()
                .map(training -> new TrainingProgramSimpleDto(training.getId(), training.getTrainingName()))
                .collect(java.util.stream.Collectors.toList());

        log.info("전체 교육 프로그램 목록 조회 성공 - count: {}", result.size());
        return ApiResponse.success(result, "교육 프로그램 목록을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 교육 완료 상태 목록 조회
     */
    @GetMapping("/trainings/completion-statuses")
    public ApiResponse<List<TrainingCompletionStatusDto>> getAllTrainingCompletionStatuses() {
        log.info("교육 완료 상태 목록 조회 API 호출");

        List<TrainingCompletionStatusDto> result = java.util.List.of(
                new TrainingCompletionStatusDto("true", "완료"),
                new TrainingCompletionStatusDto("false", "미완료")
        );

        log.info("교육 완료 상태 목록 조회 성공 - count: {}", result.size());
        return ApiResponse.success(result, "교육 완료 상태 목록을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 직원 교육 이력 조회
     */
    @GetMapping("/trainings/employee/{employeeId}/training-history")
    public ApiResponse<EmployeeTrainingHistoryDto> getEmployeeTrainingHistory(@PathVariable String employeeId) {
        log.info("직원 교육 이력 조회 요청 - employeeId: {}", employeeId);
        EmployeeTrainingHistoryVo vo = new EmployeeTrainingHistoryVo(employeeId);
        EmployeeTrainingHistoryDto result = trainingService.getEmployeeTrainingHistory(vo);
        log.info("직원 교육 이력 조회 성공 - employeeId: {}, completedCount: {}, requiredMissingCount: {}",
                employeeId, result.getCompletedCount(), result.getRequiredMissingCount());
        return ApiResponse.success(result, "직원 교육 이력 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 직원 교육 현황 목록 조회
     */
    @GetMapping("/trainings")
    public ApiResponse<EmployeeTrainingListResponseDto> getEmployeeTrainingList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("직원 교육 현황 목록 조회 요청 - department: {}, position: {}, name: {}, page: {}, size: {}",
                department, position, name, page, size);

        EmployeeTrainingSearchConditionVo condition = new EmployeeTrainingSearchConditionVo(department, position, name);
        Pageable pageable = PageRequest.of(page, size);
        EmployeeTrainingListResponseDto result = trainingService.getEmployeeTrainingList(condition, pageable);

        log.info("직원 교육 현황 목록 조회 성공 - totalElements: {}", result.getPage().getTotalElements());
        return ApiResponse.success(result, "목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 직원 교육 현황 통계 조회
     */
    @GetMapping("/trainings/training-status")
    public ApiResponse<TrainingStatusResponseDto> getTrainingStatusList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("직원 교육 현황 통계 조회 API 호출 - department: {}, position: {}, name: {}, page: {}, size: {}",
                department, position, name, page, size);

        TrainingStatusSearchConditionVo condition = new TrainingStatusSearchConditionVo(department, position, name);
        Pageable pageable = PageRequest.of(page, size);
        TrainingStatusResponseDto result = trainingService.getTrainingStatusList(condition, pageable);

        log.info("직원 교육 현황 통계 조회 성공 - totalElements: {}", result.getPage().getTotalElements());
        return ApiResponse.success(result, "목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 직원별 교육 요약 정보 조회
     */
    @GetMapping("/trainings/training/employee/{employeeId}")
    public ApiResponse<EmployeeTrainingSummaryDto> getEmployeeTrainingSummary(@PathVariable String employeeId) {
        log.info("직원별 교육 요약 정보 조회 API 호출 - employeeId: {}", employeeId);
        EmployeeTrainingSummaryDto result = trainingService.getEmployeeTrainingSummary(employeeId);
        log.info("직원별 교육 요약 정보 조회 성공 - employeeId: {}, employeeName: {}", employeeId, result.getEmployeeName());
        return ApiResponse.success(result, "직원 교육 이력 조회에 성공했습니다.", HttpStatus.OK);
    }

    // ==================== Time Records ====================

    /**
     * 근태 기록 상세 정보 조회
     */
    @GetMapping("/time-records/time-record/{timerecordId}")
    public ApiResponse<TimeRecordDetailDto> getTimeRecordDetail(@PathVariable String timerecordId) {
        log.info("근태 기록 상세 정보 조회 요청 - timerecordId: {}", timerecordId);
        TimeRecordDetailVo vo = new TimeRecordDetailVo(timerecordId);
        TimeRecordDetailDto result = timeRecordService.getTimeRecordDetail(vo);
        log.info("근태 기록 상세 정보 조회 성공 - timerecordId: {}, employeeName: {}, status: {}",
                timerecordId, result.getEmployee().getEmployeeName(), result.getStatusCode());
        return ApiResponse.success(result, "근태 기록 상세 정보 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 근태 기록 수정
     */
    @PatchMapping("/time-records/time-record/{timerecordId}")
    public ApiResponse<Void> updateTimeRecord(
            @PathVariable String timerecordId,
            @RequestBody UpdateTimeRecordDto requestDto) {
        log.info("근태 기록 수정 요청 - timerecordId: {}, employeeId: {}", timerecordId, requestDto.getEmployeeId());
        timeRecordService.updateTimeRecord(timerecordId, requestDto);
        log.info("근태 기록 수정 성공 - timerecordId: {}", timerecordId);
        return ApiResponse.success(null, "근태 기록이 수정되었습니다.", HttpStatus.OK);
    }

    /**
     * 근태 기록 목록 조회
     */
    @GetMapping("/time-records/time-record")
    public ApiResponse<Page<TimeRecordListItemDto>> getAttendanceList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("근태 기록 목록 조회 요청 - department: {}, position: {}, name: {}, date: {}, page: {}, size: {}",
                department, position, name, date, page, size);

        AttendanceSearchConditionVo condition = new AttendanceSearchConditionVo(department, position, name, date);
        Pageable pageable = PageRequest.of(page, size);
        Page<TimeRecordListItemDto> result = timeRecordService.getAttendanceList(condition, pageable);

        log.info("근태 기록 목록 조회 성공 - totalElements: {}, totalPages: {}", result.getTotalElements(), result.getTotalPages());
        return ApiResponse.success(result, "근태 기록 조회에 성공했습니다.", HttpStatus.OK);
    }

    // ==================== Attendance ====================

    /**
     * 출퇴근 기록 조회
     */
    @GetMapping("/attendance")
    public ApiResponse<Page<AttendanceListItemDto>> getAttendanceHistoryList(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("출퇴근 기록 조회 API 호출 - employeeId: {}, startDate: {}, endDate: {}, status: {}, page: {}, size: {}",
                employeeId, startDate, endDate, status, page, size);

        AttendanceListSearchConditionVo condition = new AttendanceListSearchConditionVo(employeeId, startDate, endDate, status);
        Pageable pageable = PageRequest.of(page, size);
        Page<AttendanceListItemDto> result = attendanceService.getAttendanceList(condition, pageable);

        log.info("출퇴근 기록 조회 성공 - totalElements: {}, totalPages: {}", result.getTotalElements(), result.getTotalPages());
        return ApiResponse.success(result, "출퇴근 기록을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 출근 처리 (InternelUser ID 기반)
     */
    @PatchMapping("/attendance/check-in")
    public ApiResponse<Void> checkIn(@RequestBody CheckInRequestDto requestDto) {
        log.info("출근 처리 API 호출 - internelUserId: {}", requestDto.getEmployeeId());
        attendanceService.checkInByInternelUserId(requestDto.getEmployeeId());
        log.info("출근 처리 성공 - internelUserId: {}", requestDto.getEmployeeId());
        return ApiResponse.success(null, "출근 처리가 완료되었습니다.", HttpStatus.OK);
    }

    /**
     * 퇴근 처리 (InternelUser ID 기반)
     */
    @PatchMapping("/attendance/check-out")
    public ApiResponse<Void> checkOut(@RequestBody CheckOutRequestDto requestDto) {
        log.info("퇴근 처리 API 호출 - internelUserId: {}", requestDto.getEmployeeId());
        attendanceService.checkOutByInternelUserId(requestDto.getEmployeeId());
        log.info("퇴근 처리 성공 - internelUserId: {}", requestDto.getEmployeeId());
        return ApiResponse.success(null, "퇴근 처리가 완료되었습니다.", HttpStatus.OK);
    }

    /**
     * InternelUser ID로 출근 처리
     */
    @PatchMapping("/attendance/check-in-by-internel-user")
    public ApiResponse<Void> checkInByInternelUserId(@RequestBody CheckInRequestDto requestDto) {
        log.info("InternelUser ID로 출근 처리 API 호출 - internelUserId: {}", requestDto.getEmployeeId());
        attendanceService.checkInByInternelUserId(requestDto.getEmployeeId());
        log.info("InternelUser ID로 출근 처리 성공 - internelUserId: {}", requestDto.getEmployeeId());
        return ApiResponse.success(null, "출근 처리가 완료되었습니다.", HttpStatus.OK);
    }

    /**
     * InternelUser ID로 퇴근 처리
     */
    @PatchMapping("/attendance/check-out-by-internel-user")
    public ApiResponse<Void> checkOutByInternelUserId(@RequestBody CheckOutRequestDto requestDto) {
        log.info("InternelUser ID로 퇴근 처리 API 호출 - internelUserId: {}", requestDto.getEmployeeId());
        attendanceService.checkOutByInternelUserId(requestDto.getEmployeeId());
        log.info("InternelUser ID로 퇴근 처리 성공 - internelUserId: {}", requestDto.getEmployeeId());
        return ApiResponse.success(null, "퇴근 처리가 완료되었습니다.", HttpStatus.OK);
    }

    /**
     * InternelUser ID로 출퇴근 기록 목록 조회
     */
    @GetMapping("/employees/{internelUserId}/attendance-records")
    public ApiResponse<List<AttendanceRecordDto>> getAttendanceRecordsByInternelUserId(@PathVariable String internelUserId) {
        log.info("InternelUser ID로 출퇴근 기록 목록 조회 API 호출 - internelUserId: {}", internelUserId);
        List<AttendanceRecordDto> result = attendanceService.getAttendanceRecordsByInternelUserId(internelUserId);
        log.info("InternelUser ID로 출퇴근 기록 목록 조회 성공 - internelUserId: {}, recordCount: {}", internelUserId, result.size());
        return ApiResponse.success(result, "출퇴근 기록 목록을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 출퇴근 상태 목록 조회 (enum 전체)
     */
    @GetMapping("/attendance/statuses")
    public ApiResponse<List<AttendanceStatusDto>> getAllAttendanceStatuses() {
        log.info("출퇴근 상태 목록 조회 API 호출");

        List<AttendanceStatusDto> result = java.util.Arrays.stream(AttendanceStatus.values())
                .map(status -> new AttendanceStatusDto(
                        status.name(),
                        getAttendanceStatusDescription(status)
                ))
                .collect(java.util.stream.Collectors.toList());

        log.info("출퇴근 상태 목록 조회 성공 - count: {}", result.size());
        return ApiResponse.success(result, "출퇴근 상태 목록을 조회했습니다.", HttpStatus.OK);
    }

    private String getAttendanceStatusDescription(AttendanceStatus status) {
        return switch (status) {
            case NORMAL -> "정상 근무 (API 응답용)";
            case ON_TIME -> "정상 출근";
            case LATE -> "지각";
            case ON_LEAVE -> "휴가";
            case PRESENT -> "출근 (정상 근무)";
            case ABSENT -> "결근";
            case EARLY_LEAVE -> "조퇴";
            case HOLIDAY -> "휴일 (공휴일/주말 등)";
            case OFFICIAL_TRIP -> "출장";
        };
    }

    // ==================== HRM Employee Info ====================

    /**
     * HRM 직원 기본 정보 조회 (InternelUser userId 기반)
     */
    @PostMapping("/{userId}/employee")
    public ApiResponse<HrmEmployeeBasicInfoDto> getEmployeeBasicInfo(@PathVariable String userId) {
        log.info("HRM 직원 기본 정보 조회 API 호출 - userId: {}", userId);

        InternelUser internelUser = internelUserRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("직원 정보를 찾을 수 없습니다. userId: " + userId));

        HrmEmployeeBasicInfoDto result = new HrmEmployeeBasicInfoDto(
                internelUser.getUserId(),
                internelUser.getName(),
                internelUser.getPhoneNumber(),
                internelUser.getEmail()
        );

        log.info("HRM 직원 기본 정보 조회 성공 - userId: {}, name: {}", userId, internelUser.getName());
        return ApiResponse.success(result, "성공 메시지", HttpStatus.OK);
    }

    /**
     * HRM 직원 기본 정보 다중 조회 (InternelUser userIds 기반)
     */
    @PostMapping("/employees/multiple")
    public ApiResponse<List<HrmEmployeeBasicInfoDto>> getEmployeesBasicInfo(@RequestBody EmployeesMultipleRequestDto requestDto) {
        log.info("HRM 직원 기본 정보 다중 조회 API 호출 - userIds count: {}", requestDto.getUserIds().size());

        List<HrmEmployeeBasicInfoDto> result = requestDto.getUserIds().stream()
                .map(userId -> {
                    try {
                        InternelUser internelUser = internelUserRepository.findByUserId(userId)
                                .orElseThrow(() -> new RuntimeException("직원 정보를 찾을 수 없습니다. userId: " + userId));

                        return new HrmEmployeeBasicInfoDto(
                                internelUser.getUserId(),
                                internelUser.getName(),
                                internelUser.getPhoneNumber(),
                                internelUser.getEmail()
                        );
                    } catch (Exception e) {
                        log.warn("직원 정보 조회 실패 - userId: {}, error: {}", userId, e.getMessage());
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .collect(java.util.stream.Collectors.toList());

        log.info("HRM 직원 기본 정보 다중 조회 성공 - 조회된 직원 수: {}", result.size());
        return ApiResponse.success(result, "성공 메시지", HttpStatus.OK);
    }

    @PostMapping("/employee-users")
    @Operation(summary = "내부 사용자 생성", description = "내부 사용자 생성을 비동기로 처리합니다.")
    public DeferredResult<ResponseEntity<ApiResponse<CreateAuthUserResultEvent>>> createEmployeeUser(
            @RequestBody @Valid EmployeeCreateRequestDto requestDto
    ) {
        log.info("[INFO] 내부 사용자(employee) 생성 API 호출 - requestDto: {}", requestDto);

        // DeferredResult를 생성하여 30초(30000ms) 타임아웃 설정
        DeferredResult<ResponseEntity<ApiResponse<CreateAuthUserResultEvent>>> deferredResult = new DeferredResult<>(30000L);

        deferredResult.onTimeout(() -> {
            log.warn("[WARN] 내부 사용자 생성 처리 타임아웃 - email: {}", requestDto.getEmail());
            deferredResult.setResult(ResponseEntity
                    .status(HttpStatus.REQUEST_TIMEOUT)
                    .body(ApiResponse.fail("[SAGA][FAIL] 처리 시간이 초과되었습니다.", HttpStatus.REQUEST_TIMEOUT)));
        });
        employeeService.createEmployee(requestDto, deferredResult);
        return deferredResult;
    }
}
