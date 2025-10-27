package org.ever._4ever_be_business.hr.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.hr.dto.request.CreateLeaveRequestDto;
import org.ever._4ever_be_business.hr.dto.response.LeaveRequestListItemDto;
import org.ever._4ever_be_business.hr.enums.LeaveType;
import org.ever._4ever_be_business.hr.service.LeaveRequestService;
import org.ever._4ever_be_business.hr.vo.LeaveRequestSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/hrm/leave")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    /**
     * 휴가 신청 목록 조회 (deprecated - /hrm/leave-request로 이동)
     *
     * @param department 부서 ID 필터
     * @param position   직급 ID 필터
     * @param name       이름 검색
     * @param type       휴가 유형 필터 (ANNUAL/SICK)
     * @param sortOrder  정렬 순서 (DESC/ASC)
     * @param page       페이지 번호 (0부터 시작)
     * @param size       페이지 크기
     * @return ApiResponse<Page<LeaveRequestListItemDto>>
     */
    @GetMapping("/request")
    public ApiResponse<Page<LeaveRequestListItemDto>> getLeaveRequestList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LeaveType type,
            @RequestParam(required = false, defaultValue = "DESC") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("휴가 신청 목록 조회 API 호출 - department: {}, position: {}, name: {}, type: {}, sortOrder: {}, page: {}, size: {}",
                department, position, name, type, sortOrder, page, size);

        // 1. 검색 조건 VO 생성
        LeaveRequestSearchConditionVo condition = new LeaveRequestSearchConditionVo(
                department, position, name, type, sortOrder
        );

        // 2. 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. Service 호출
        Page<LeaveRequestListItemDto> result = leaveRequestService.getLeaveRequestList(condition, pageable);

        log.info("휴가 신청 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return ApiResponse.success(result, "휴가 신청 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 휴가 신청
     *
     * @param requestDto 휴가 신청 정보
     * @return ApiResponse<Void>
     */
    @PostMapping("/request")
    public ApiResponse<Void> createLeaveRequest(@RequestBody CreateLeaveRequestDto requestDto) {
        log.info("휴가 신청 API 호출 - employeeId: {}, leaveType: {}, startDate: {}, endDate: {}",
                requestDto.getEmployeeId(), requestDto.getLeaveType(),
                requestDto.getStartDate(), requestDto.getEndDate());

        leaveRequestService.createLeaveRequest(requestDto);

        log.info("휴가 신청 성공 - employeeId: {}", requestDto.getEmployeeId());

        return ApiResponse.success(null, "휴가 신청이 완료되었습니다.", HttpStatus.OK);
    }

    /**
     * 휴가 신청 승인
     *
     * @param requestId 휴가 신청 ID
     * @return ApiResponse<Void>
     */
    @PatchMapping("/request/{requestId}/release")
    public ApiResponse<Void> approveLeaveRequest(@PathVariable String requestId) {
        log.info("휴가 신청 승인 API 호출 - requestId: {}", requestId);

        leaveRequestService.approveLeaveRequest(requestId);

        log.info("휴가 신청 승인 성공 - requestId: {}", requestId);

        return ApiResponse.success(null, "휴가 신청이 승인되었습니다.", HttpStatus.OK);
    }

    /**
     * 휴가 신청 반려
     *
     * @param requestId 휴가 신청 ID
     * @return ApiResponse<Void>
     */
    @PatchMapping("/request/{requestId}/reject")
    public ApiResponse<Void> rejectLeaveRequest(@PathVariable String requestId) {
        log.info("휴가 신청 반려 API 호출 - requestId: {}", requestId);

        leaveRequestService.rejectLeaveRequest(requestId);

        log.info("휴가 신청 반려 성공 - requestId: {}", requestId);

        return ApiResponse.success(null, "휴가 신청이 반려되었습니다.", HttpStatus.OK);
    }
}
