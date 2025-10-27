package org.ever._4ever_be_business.tam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.tam.dto.request.CheckInRequestDto;
import org.ever._4ever_be_business.tam.dto.request.CheckOutRequestDto;
import org.ever._4ever_be_business.tam.dto.response.AttendanceListItemDto;
import org.ever._4ever_be_business.tam.service.AttendanceService;
import org.ever._4ever_be_business.tam.vo.AttendanceListSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/hrm/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 출퇴근 기록 조회
     *
     * @param employeeId 직원 ID
     * @param startDate  시작일 (YYYY-MM-DD)
     * @param endDate    종료일 (YYYY-MM-DD)
     * @param status     상태 (NORMAL, LATE, EARLY_LEAVE, ABSENT)
     * @param page       페이지 번호 (0부터 시작)
     * @param size       페이지 크기
     * @return ApiResponse<Page<AttendanceListItemDto>>
     */
    @GetMapping
    public ApiResponse<Page<AttendanceListItemDto>> getAttendanceList(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("출퇴근 기록 조회 API 호출 - employeeId: {}, startDate: {}, endDate: {}, status: {}, page: {}, size: {}",
                employeeId, startDate, endDate, status, page, size);

        // 1. 검색 조건 VO 생성
        AttendanceListSearchConditionVo condition = new AttendanceListSearchConditionVo(
                employeeId, startDate, endDate, status
        );

        // 2. 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. Service 호출
        Page<AttendanceListItemDto> result = attendanceService.getAttendanceList(condition, pageable);

        log.info("출퇴근 기록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return ApiResponse.success(result, "출퇴근 기록을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 출근 처리
     *
     * @param requestDto 출근 요청
     * @return ApiResponse<Void>
     */
    @PatchMapping("/check-in")
    public ApiResponse<Void> checkIn(@RequestBody CheckInRequestDto requestDto) {
        log.info("출근 처리 API 호출 - employeeId: {}", requestDto.getEmployeeId());

        attendanceService.checkIn(requestDto.getEmployeeId());

        log.info("출근 처리 성공 - employeeId: {}", requestDto.getEmployeeId());

        return ApiResponse.success(null, "출근 처리가 완료되었습니다.", HttpStatus.OK);
    }

    /**
     * 퇴근 처리
     *
     * @param requestDto 퇴근 요청
     * @return ApiResponse<Void>
     */
    @PatchMapping("/check-out")
    public ApiResponse<Void> checkOut(@RequestBody CheckOutRequestDto requestDto) {
        log.info("퇴근 처리 API 호출 - employeeId: {}", requestDto.getEmployeeId());

        attendanceService.checkOut(requestDto.getEmployeeId());

        log.info("퇴근 처리 성공 - employeeId: {}", requestDto.getEmployeeId());

        return ApiResponse.success(null, "퇴근 처리가 완료되었습니다.", HttpStatus.OK);
    }
}
