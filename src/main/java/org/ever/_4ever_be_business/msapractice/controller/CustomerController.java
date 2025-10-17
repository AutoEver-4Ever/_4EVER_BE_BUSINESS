package org.ever._4ever_be_business.msapractice.controller;

import org.ever._4ever_be_business.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.msapractice.dto.CreateCustomerRequestDto;
import org.ever._4ever_be_business.msapractice.dto.CustomerResponseDto;
import org.ever._4ever_be_business.msapractice.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/business/sd/customers")
@RequiredArgsConstructor
@Tag(name = "Customer API", description = "고객 관련 API")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "고객 정보 등록", description = "신규 고객 정보를 등록합니다")
    public DeferredResult<ResponseEntity<ApiResponse<Void>>> createCustomer(
            @Valid @RequestBody CreateCustomerRequestDto requestDto) {
        log.info("고객 정보 등록 요청 수신: {}", requestDto.getCompanyName());
        
        // DeferredResult 생성 (타임아웃: 30초)
        DeferredResult<ResponseEntity<ApiResponse<Void>>> deferredResult =
                new DeferredResult<>(30000L);
        
        // 타임아웃 처리
        deferredResult.onTimeout(() -> {
            log.warn("고객 정보 등록 요청 타임아웃: {}", requestDto.getCompanyName());
            deferredResult.setResult(ResponseEntity
                    .status(HttpStatus.REQUEST_TIMEOUT)
                    .body(ApiResponse.fail("처리 시간이 초과되었습니다. 다시 시도해주세요.", HttpStatus.REQUEST_TIMEOUT)));
        });
        
        // 비동기 처리 시작
        customerService.createCustomerAsync(requestDto, deferredResult);
        
        return deferredResult;
    }
}