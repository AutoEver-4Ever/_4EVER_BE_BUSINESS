package org.ever._4ever_be_business.hr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.dto.request.CreateLeaveRequestDto;
import org.ever._4ever_be_business.hr.dto.response.LeaveRequestListItemDto;
import org.ever._4ever_be_business.hr.dto.response.RemainingLeaveDaysDto;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.InternelUser;
import org.ever._4ever_be_business.hr.entity.LeaveRequest;
import org.ever._4ever_be_business.hr.enums.LeaveRequestStatus;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.repository.InternelUserRepository;
import org.ever._4ever_be_business.hr.repository.LeaveRequestRepository;
import org.ever._4ever_be_business.hr.service.LeaveRequestService;
import org.ever._4ever_be_business.hr.vo.LeaveRequestSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final InternelUserRepository internelUserRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<LeaveRequestListItemDto> getLeaveRequestList(LeaveRequestSearchConditionVo condition, Pageable pageable) {
        log.info("휴가 신청 목록 조회 요청 - departmentId: {}, positionId: {}, name: {}, type: {}, sortOrder: {}",
                condition.getDepartmentId(), condition.getPositionId(), condition.getName(),
                condition.getType(), condition.getSortOrder());

        // 1. Repository에서 기본 데이터 조회 (remainingLeaveDays는 임시 값)
        Page<LeaveRequestListItemDto> rawResult = leaveRequestRepository.findLeaveRequestList(condition, pageable);

        // 2. 각 직원별로 실제 remainingLeaveDays 계산
        List<LeaveRequestListItemDto> updatedContent = rawResult.getContent().stream()
                .map(item -> {
                    String employeeId = item.getEmployee().getEmployeeId();

                    // 승인된 휴가 일수 합계 조회
                    Integer approvedDays = leaveRequestRepository.sumApprovedLeaveDaysByEmployeeId(
                            employeeId, LeaveRequestStatus.APPROVED);

                    // 남은 연차 = 18 - 승인된 휴가 일수
                    int remainingLeaveDays = 18 - (approvedDays != null ? approvedDays : 0);

                    // 새로운 DTO 생성 (remainingLeaveDays만 업데이트)
                    return new LeaveRequestListItemDto(
                            item.getLeaveRequestId(),
                            item.getEmployee(),
                            item.getLeaveType(),
                            item.getStartDate(),
                            item.getEndDate(),
                            item.getNumberOfLeaveDays(),
                            remainingLeaveDays  // 계산된 값으로 대체
                    );
                })
                .collect(Collectors.toList());

        // 3. 새로운 Page 객체 생성
        Page<LeaveRequestListItemDto> result = new PageImpl<>(
                updatedContent,
                rawResult.getPageable(),
                rawResult.getTotalElements()
        );

        log.info("휴가 신청 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return result;
    }

    @Override
    @Transactional
    public void createLeaveRequest(CreateLeaveRequestDto requestDto) {
        log.info("휴가 신청 요청 - internelUserId: {}, leaveType: {}, startDate: {}, endDate: {}",
                requestDto.getInternelUserId(), requestDto.getLeaveType(),
                requestDto.getStartDate(), requestDto.getEndDate());

        // 1. InternelUser로 Employee 조회
        InternelUser internelUser = internelUserRepository.findById(requestDto.getInternelUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "내부 직원 정보를 찾을 수 없습니다."));

        Employee employee = employeeRepository.findByInternelUser(internelUser)
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

        log.info("휴가 신청 성공 - internelUserId: {}, leaveRequestId: {}, numberOfLeaveDays: {}",
                requestDto.getInternelUserId(), leaveRequest.getId(), numberOfLeaveDays);
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

    @Override
    @Transactional(readOnly = true)
    public RemainingLeaveDaysDto getRemainingLeaveDays(String userId) {
        log.info("잔여 연차 조회 요청 - userId: {}", userId);

        // 1. InternelUser 조회
        InternelUser internelUser = internelUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 2. Employee 조회
        Employee employee = employeeRepository.findByInternelUser(internelUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        // 3. 1년 전 날짜 계산
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

        // 4. 1년 이내 승인된 휴가 일수 합계 조회
        Integer usedLeaveDays = leaveRequestRepository.sumApprovedLeaveDaysInLastYear(
                employee.getId(),
                LeaveRequestStatus.APPROVED,
                oneYearAgo
        );

        // 5. 잔여 연차 계산 (기본 연차 18일 - 사용 일수)
        int used = (usedLeaveDays != null) ? usedLeaveDays : 0;
        int remaining = 18 - used;

        RemainingLeaveDaysDto result = new RemainingLeaveDaysDto(
                userId,
                18,
                used,
                remaining
        );

        log.info("잔여 연차 조회 성공 - userId: {}, usedLeaveDays: {}, remainingLeaveDays: {}",
                userId, used, remaining);

        return result;
    }
}
