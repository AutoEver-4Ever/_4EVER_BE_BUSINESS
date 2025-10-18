package org.ever._4ever_be_business.msapractice.service;

import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.msapractice.dto.CreateCustomerRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

public interface CustomerService {
    // 비동기 처리를 위한 메서드
    void createCustomerAsync(CreateCustomerRequestDto requestDto, 
            DeferredResult<ResponseEntity<ApiResponse<Void>>> deferredResult);

    boolean processServiceCompletionEvent(String customerUserId, boolean success, String transactionId);
}