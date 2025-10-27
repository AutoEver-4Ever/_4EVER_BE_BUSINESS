package org.ever._4ever_be_business.tam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.tam.dto.request.UpdateTimeRecordDto;
import org.ever._4ever_be_business.tam.dto.response.TimeRecordDetailDto;
import org.ever._4ever_be_business.tam.dto.response.TimeRecordListItemDto;
import org.ever._4ever_be_business.tam.service.TimeRecordService;
import org.ever._4ever_be_business.tam.vo.AttendanceSearchConditionVo;
import org.ever._4ever_be_business.tam.vo.TimeRecordDetailVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/hrm")
@RequiredArgsConstructor
public class TimeRecordController {

    private final TimeRecordService timeRecordService;

    /**
     * 근태 기록 상세 정보 조회
     *
     * @param timerecordId 근태 기록 ID
     * @return ApiResponse<TimeRecordDetailDto>
     */
    @GetMapping("/time-record/{timerecordId}")
    public ApiResponse<TimeRecordDetailDto> getTimeRecordDetail(
            @PathVariable String timerecordId
    ) {
        log.info("근태 기록 상세 정보 조회 요청 - timerecordId: {}", timerecordId);

        // PathVariable을 VO로 변환
        TimeRecordDetailVo vo = new TimeRecordDetailVo(timerecordId);

        // Service 호출
        TimeRecordDetailDto result = timeRecordService.getTimeRecordDetail(vo);

        log.info("근태 기록 상세 정보 조회 성공 - timerecordId: {}, employeeName: {}, status: {}",
                timerecordId, result.getEmployee().getEmployeeName(), result.getStatusCode());

        return ApiResponse.success(result, "근태 기록 상세 정보 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 근태 기록 수정
     *
     * @param timerecordId 근태 기록 ID
     * @param requestDto   수정 요청 데이터
     * @return ApiResponse<Void>
     */
    @PatchMapping("/time-record/{timerecordId}")
    public ApiResponse<Void> updateTimeRecord(
            @PathVariable String timerecordId,
            @RequestBody UpdateTimeRecordDto requestDto
    ) {
        log.info("근태 기록 수정 요청 - timerecordId: {}, employeeId: {}", timerecordId, requestDto.getEmployeeId());

        timeRecordService.updateTimeRecord(timerecordId, requestDto);

        log.info("근태 기록 수정 성공 - timerecordId: {}", timerecordId);

        return ApiResponse.success(null, "근태 기록이 수정되었습니다.", HttpStatus.OK);
    }

    /**
     * 근태 기록 목록 조회 (검색 + 페이징)
     *
     * @param department 부서 ID (선택)
     * @param position   직급 ID (선택)
     * @param name       직원명 (선택, 부분 검색)
     * @param date       근무일자 (필수, yyyy-MM-dd)
     * @param page       페이지 번호 (0부터 시작)
     * @param size       페이지 크기
     * @return ApiResponse<Page < TimeRecordListItemDto>>
     */
    @GetMapping("/time-record")
    public ApiResponse<Page<TimeRecordListItemDto>> getAttendanceList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("근태 기록 목록 조회 요청 - department: {}, position: {}, name: {}, date: {}, page: {}, size: {}",
                department, position, name, date, page, size);

        // 1. 검색 조건 생성
        AttendanceSearchConditionVo condition = new AttendanceSearchConditionVo(department, position, name, date);

        // 2. 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. Service 호출
        Page<TimeRecordListItemDto> result = timeRecordService.getAttendanceList(condition, pageable);

        log.info("근태 기록 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return ApiResponse.success(result, "근태 기록 조회에 성공했습니다.", HttpStatus.OK);
    }
}
