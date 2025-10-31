package org.ever._4ever_be_business.sd.service;

import org.ever._4ever_be_business.sd.dto.request.CreateQuotationRequestDto;
import org.ever._4ever_be_business.sd.dto.request.InventoryCheckRequestDto;
import org.ever._4ever_be_business.sd.dto.response.InventoryCheckResponseDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationDetailDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationListItemDto;
import org.ever._4ever_be_business.sd.vo.QuotationDetailVo;
import org.ever._4ever_be_business.sd.vo.QuotationSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuotationService {
    /**
     * 견적 상세 조회
     *
     * @param vo 견적 조회 조건
     * @return 견적 상세 정보
     */
    QuotationDetailDto getQuotationDetail(QuotationDetailVo vo);

    /**
     * 견적 목록 조회 (검색 + 페이징)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return 견적 목록
     */
    Page<QuotationListItemDto> getQuotationList(QuotationSearchConditionVo condition, Pageable pageable);

    /**
     * 견적서 생성
     *
     * @param dto 견적서 생성 요청
     * @return 생성된 견적서 ID
     */
    String createQuotation(CreateQuotationRequestDto dto);

    /**
     * 견적서 승인 및 주문 생성
     *
     * @param quotationId 견적서 ID
     * @param employeeId 승인한 직원 ID
     */
    void approveQuotation(String quotationId, String employeeId);

    /**
     * 견적서 검토 확정
     *
     * @param quotationId 견적서 ID
     */
    void confirmQuotation(String quotationId);

    /**
     * 견적서 거부
     *
     * @param quotationId 견적서 ID
     * @param reason 거부 사유
     */
    void rejectQuotation(String quotationId, String reason);

    /**
     * 재고 확인
     *
     * @param requestDto 재고 확인 요청
     * @return 재고 확인 결과
     */
    InventoryCheckResponseDto checkInventory(InventoryCheckRequestDto requestDto);
}
