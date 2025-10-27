package org.ever._4ever_be_business.tam.service;

import org.ever._4ever_be_business.tam.dto.response.AttendanceListItemDto;
import org.ever._4ever_be_business.tam.vo.AttendanceListSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AttendanceService {
    /**
     * 출퇴근 기록 목록 조회
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<AttendanceListItemDto>
     */
    Page<AttendanceListItemDto> getAttendanceList(AttendanceListSearchConditionVo condition, Pageable pageable);

    /**
     * 출근 처리
     *
     * @param employeeId 직원 ID
     */
    void checkIn(String employeeId);

    /**
     * 퇴근 처리
     *
     * @param employeeId 직원 ID
     */
    void checkOut(String employeeId);
}
