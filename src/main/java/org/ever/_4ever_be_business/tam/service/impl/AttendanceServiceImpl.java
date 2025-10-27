package org.ever._4ever_be_business.tam.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.tam.dao.AttendanceDAO;
import org.ever._4ever_be_business.tam.dto.response.AttendanceListItemDto;
import org.ever._4ever_be_business.tam.entity.Attendance;
import org.ever._4ever_be_business.tam.repository.AttendanceRepository;
import org.ever._4ever_be_business.tam.service.AttendanceService;
import org.ever._4ever_be_business.tam.vo.AttendanceListSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceDAO attendanceDAO;
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceListItemDto> getAttendanceList(AttendanceListSearchConditionVo condition, Pageable pageable) {
        log.info("출퇴근 기록 조회 요청 - employeeId: {}, startDate: {}, endDate: {}, status: {}",
                condition.getEmployeeId(), condition.getStartDate(), condition.getEndDate(), condition.getStatus());

        Page<AttendanceListItemDto> result = attendanceDAO.findAttendanceList(condition, pageable);

        log.info("출퇴근 기록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return result;
    }

    @Override
    @Transactional
    public void checkIn(String employeeId) {
        log.info("출근 처리 요청 - employeeId: {}", employeeId);

        // 1. Employee 조회
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR));

        // 2. 오늘 날짜의 출퇴근 기록 조회
        Optional<Attendance> existingAttendance = attendanceRepository.findTodayAttendanceByEmployeeId(employeeId);

        Attendance attendance;
        if (existingAttendance.isPresent()) {
            // 이미 오늘 기록이 있으면 출근 시간 업데이트
            attendance = existingAttendance.get();
            attendance.checkIn();
            log.info("기존 출퇴근 기록 출근 시간 업데이트 - attendanceId: {}", attendance.getId());
        } else {
            // 오늘 기록이 없으면 새로 생성
            attendance = Attendance.createForCheckIn(employee);
            log.info("새로운 출퇴근 기록 생성");
        }

        // 3. 저장
        attendanceRepository.save(attendance);

        log.info("출근 처리 성공 - employeeId: {}, attendanceId: {}", employeeId, attendance.getId());
    }

    @Override
    @Transactional
    public void checkOut(String employeeId) {
        log.info("퇴근 처리 요청 - employeeId: {}", employeeId);

        // 1. 오늘 날짜의 출퇴근 기록 조회
        Attendance attendance = attendanceRepository.findTodayAttendanceByEmployeeId(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR));

        // 2. 퇴근 처리
        attendance.checkOut();

        // 3. 저장
        attendanceRepository.save(attendance);

        log.info("퇴근 처리 성공 - employeeId: {}, attendanceId: {}, workMinutes: {}, overtimeMinutes: {}",
                employeeId, attendance.getId(), attendance.getWorkMinutes(), attendance.getOvertimeMinutes());
    }
}
