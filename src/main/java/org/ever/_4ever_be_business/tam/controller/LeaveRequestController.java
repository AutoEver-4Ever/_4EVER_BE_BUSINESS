package org.ever._4ever_be_business.tam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.hr.enums.VacationType;
import org.ever._4ever_be_business.tam.dto.response.LeaveRequestListItemDto;
import org.ever._4ever_be_business.tam.service.LeaveRequestService;
import org.ever._4ever_be_business.tam.vo.LeaveRequestSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("tamLeaveRequestController")
@RequestMapping("/tam")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    /**
     * 휴가 신청 목록 조회 (검색 + 페이징)
     *
     * @param department 부서명 (선택)
     * @param position   직급명 (선택)
     * @param name       직원명 (선택, 부분 검색)
     * @param type       휴가 유형 (선택, ANNUAL | SICK 등)
     * @param page       페이지 번호 (0부터 시작)
     * @param size       페이지 크기
     * @return ApiResponse<Page < LeaveRequestListItemDto>>
     */
    @GetMapping("/leave-request")
    public ApiResponse<Page<LeaveRequestListItemDto>> getLeaveRequestList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) VacationType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("휴가 신청 목록 조회 요청 - department: {}, position: {}, name: {}, type: {}, page: {}, size: {}",
                department, position, name, type, page, size);

        // 1. 검색 조건 생성
        LeaveRequestSearchConditionVo condition = new LeaveRequestSearchConditionVo(department, position, name, type);

        // 2. 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. Service 호출
        Page<LeaveRequestListItemDto> result = leaveRequestService.getLeaveRequestList(condition, pageable);

        log.info("휴가 신청 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return ApiResponse.success(result, "휴가 신청 목록 조회에 성공했습니다.", HttpStatus.OK);
    }
}
