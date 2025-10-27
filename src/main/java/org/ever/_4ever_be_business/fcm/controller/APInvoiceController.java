package org.ever._4ever_be_business.fcm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.fcm.dto.request.UpdateVoucherStatusDto;
import org.ever._4ever_be_business.fcm.dto.response.APInvoiceDetailDto;
import org.ever._4ever_be_business.fcm.service.APInvoiceService;
import org.ever._4ever_be_business.fcm.service.VoucherStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/fcm/invoice/ap")
@RequiredArgsConstructor
public class APInvoiceController {

    private final APInvoiceService apInvoiceService;
    private final VoucherStatusService voucherStatusService;

    /**
     * AP 전표 상세 정보 조회
     *
     * @param invoiceId 전표 ID
     * @return ApiResponse<APInvoiceDetailDto>
     */
    @GetMapping("/{invoiceId}")
    public ApiResponse<APInvoiceDetailDto> getAPInvoiceDetail(
            @PathVariable String invoiceId
    ) {
        log.info("AP 전표 상세 정보 조회 API 호출 - invoiceId: {}", invoiceId);

        APInvoiceDetailDto result = apInvoiceService.getAPInvoiceDetail(invoiceId);

        log.info("AP 전표 상세 정보 조회 성공 - invoiceId: {}, invoiceNumber: {}",
                invoiceId, result.getInvoiceNumber());

        return ApiResponse.success(result, "매입 전표 상세 정보 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 바우처 상태 수동 업데이트
     *
     * @param requestDto 바우처 ID와 상태 코드
     * @return ApiResponse<Void>
     */
    @PostMapping("/receivable/request")
    public ApiResponse<Void> updateVoucherStatus(
            @RequestBody UpdateVoucherStatusDto requestDto
    ) {
        log.info("바우처 상태 업데이트 API 호출 - voucherId: {}, statusCode: {}",
                requestDto.getVoucherId(), requestDto.getStatusCode());

        voucherStatusService.updateVoucherStatus(requestDto.getVoucherId(), requestDto.getStatusCode());

        log.info("바우처 상태 업데이트 성공 - voucherId: {}", requestDto.getVoucherId());

        return ApiResponse.success(null, "바우처 상태가 업데이트되었습니다.", HttpStatus.OK);
    }
}
