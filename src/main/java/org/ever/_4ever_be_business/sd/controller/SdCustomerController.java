package org.ever._4ever_be_business.sd.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.sd.dto.request.CreateCustomerRequestDto;
import org.ever._4ever_be_business.sd.dto.request.UpdateCustomerRequestDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerDetailDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerListResponseDto;
import org.ever._4ever_be_business.sd.service.SdCustomerService;
import org.ever._4ever_be_business.sd.vo.CustomerDetailVo;
import org.ever._4ever_be_business.sd.vo.CustomerSearchConditionVo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/sd/customers")
@RequiredArgsConstructor
public class SdCustomerController {

    private final SdCustomerService customerService;

    /**
     * 고객사 목록 조회 (검색 + 페이징)
     *
     * @param status  상태 필터 (ALL, ACTIVE, DEACTIVE)
     * @param type    검색 타입 (customerNumber, customerName, managerName)
     * @param search  검색어
     * @param page    페이지 번호 (0부터 시작)
     * @param size    페이지 크기
     * @return ApiResponse<CustomerListResponseDto>
     */
    @GetMapping
    public ApiResponse<CustomerListResponseDto> getCustomerList(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("고객사 목록 조회 API 호출 - status: {}, type: {}, search: {}, page: {}, size: {}",
                status, type, search, page, size);

        // 1. 검색 조건 VO 생성
        CustomerSearchConditionVo condition = new CustomerSearchConditionVo(status, type, search);

        // 2. 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. Service 호출
        CustomerListResponseDto result = customerService.getCustomerList(condition, pageable);

        log.info("고객사 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getPage().getTotalElements(), result.getPage().getTotalPages());

        return ApiResponse.success(result, "고객사 목록을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 고객사 등록
     *
     * @param dto 고객사 등록 요청 정보
     * @return ApiResponse<Map<String, String>> - customerId 반환
     */
    @PostMapping
    public ApiResponse<Map<String, String>> createCustomer(@RequestBody CreateCustomerRequestDto dto) {
        log.info("고객사 등록 API 호출 - companyName: {}, businessNumber: {}",
                dto.getCompanyName(), dto.getBusinessNumber());

        // Service 호출
        String customerId = customerService.createCustomer(dto);

        log.info("고객사 등록 성공 - customerId: {}", customerId);

        return ApiResponse.success(
                Map.of("customerId", customerId),
                "고객사가 등록되었습니다.",
                HttpStatus.CREATED
        );
    }

    /**
     * 고객사 상세 정보 조회
     *
     * @param customerId 고객사 ID
     * @return ApiResponse<CustomerDetailDto>
     */
    @GetMapping("/{customerId}")
    public ApiResponse<CustomerDetailDto> getCustomerDetail(@PathVariable String customerId) {
        log.info("고객사 상세 정보 조회 API 호출 - customerId: {}", customerId);

        // 1. VO 생성
        CustomerDetailVo vo = new CustomerDetailVo(customerId);

        // 2. Service 호출
        CustomerDetailDto result = customerService.getCustomerDetail(vo);

        log.info("고객사 상세 정보 조회 성공 - customerId: {}, customerName: {}",
                customerId, result.getCustomerName());

        return ApiResponse.success(result, "고객사 상세 정보를 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 고객사 정보 수정
     *
     * @param customerId 고객사 ID
     * @param dto 수정할 고객사 정보
     * @return ApiResponse<Void>
     */
    @PatchMapping("/{customerId}")
    public ApiResponse<Void> updateCustomer(
            @PathVariable String customerId,
            @RequestBody UpdateCustomerRequestDto dto) {
        log.info("고객사 정보 수정 API 호출 - customerId: {}, customerName: {}",
                customerId, dto.getCustomerName());

        customerService.updateCustomer(customerId, dto);

        log.info("고객사 정보 수정 성공 - customerId: {}", customerId);

        return ApiResponse.success(null, "고객사 정보가 수정되었습니다.", HttpStatus.OK);
    }

    /**
     * 고객사 삭제 (Soft Delete)
     *
     * @param customerId 고객사 ID
     * @return ApiResponse<Void>
     */
    @DeleteMapping("/{customerId}")
    public ApiResponse<Void> deleteCustomer(@PathVariable String customerId) {
        log.info("고객사 삭제 API 호출 - customerId: {}", customerId);

        customerService.deleteCustomer(customerId);

        log.info("고객사 삭제 성공 (Soft Delete) - customerId: {}", customerId);

        return ApiResponse.success(null, "고객사가 삭제되었습니다.", HttpStatus.OK);
    }
}
