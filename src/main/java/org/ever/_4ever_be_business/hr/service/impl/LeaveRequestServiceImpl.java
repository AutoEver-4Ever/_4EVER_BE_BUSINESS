package org.ever._4ever_be_business.hr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.dto.request.CreateLeaveRequestDto;
import org.ever._4ever_be_business.hr.dto.response.LeaveRequestListItemDto;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.LeaveRequest;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.repository.LeaveRequestRepository;
import org.ever._4ever_be_business.hr.service.LeaveRequestService;
import org.ever._4ever_be_business.hr.vo.LeaveRequestSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<LeaveRequestListItemDto> getLeaveRequestList(LeaveRequestSearchConditionVo condition, Pageable pageable) {
        log.info("휴가 신청 목록 조회 요청 - departmentId: {}, positionId: {}, name: {}, type: {}, sortOrder: {}",
                condition.getDepartmentId(), condition.getPositionId(), condition.getName(),
                condition.getType(), condition.getSortOrder());

        Page<LeaveRequestListItemDto> result = leaveRequestRepository.findLeaveRequestList(condition, pageable);

        log.info("휴가 신청 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return result;
    }

    @Override
    @Transactional
    public void createLeaveRequest(CreateLeaveRequestDto requestDto) {
        log.info("휴가 신청 요청 - employeeId: {}, leaveType: {}, startDate: {}, endDate: {}",
                requestDto.getEmployeeId(), requestDto.getLeaveType(),
                requestDto.getStartDate(), requestDto.getEndDate());

        // 1. Employee 조회
        Employee employee = employeeRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        // 2. 날짜 파싱 (YYYY-MM-DD 형식)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(requestDto.getStartDate(), formatter);
        LocalDate endDate = LocalDate.parse(requestDto.getEndDate(), formatter);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atStartOfDay();

        // 3. 휴가 일수 계산 (시작일과 종료일 포함)
        int numberOfLeaveDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // 4. LeaveRequest 생성
        LeaveRequest leaveRequest = new LeaveRequest(
                employee,
                requestDto.getLeaveType(),
                startDateTime,
                endDateTime,
                numberOfLeaveDays,
                null  // reason은 요청에 없으므로 null
        );

        // 5. 저장
        leaveRequestRepository.save(leaveRequest);

        log.info("휴가 신청 성공 - employeeId: {}, leaveRequestId: {}, numberOfLeaveDays: {}",
                requestDto.getEmployeeId(), leaveRequest.getId(), numberOfLeaveDays);
    }

    @Override
    @Transactional
    public void approveLeaveRequest(String requestId) {
        log.info("휴가 신청 승인 요청 - requestId: {}", requestId);

        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "휴가 신청을 찾을 수 없습니다."));

        leaveRequest.approve();

        log.info("휴가 신청 승인 완료 - requestId: {}", requestId);
    }

    @Override
    @Transactional
    public void rejectLeaveRequest(String requestId) {
        log.info("휴가 신청 반려 요청 - requestId: {}", requestId);

        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "휴가 신청을 찾을 수 없습니다."));

        leaveRequest.reject();

        log.info("휴가 신청 반려 완료 - requestId: {}", requestId);
    }
}
