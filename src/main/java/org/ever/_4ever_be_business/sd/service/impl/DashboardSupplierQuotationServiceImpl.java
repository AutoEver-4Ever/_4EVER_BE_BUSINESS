package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.integration.port.SupplierCompanyServicePort;
import org.ever._4ever_be_business.sd.dto.request.SupplierQuotationRequestDto;
import org.ever._4ever_be_business.sd.dto.response.ScmQuotationListItemDto;
import org.ever._4ever_be_business.sd.dto.response.SupplierQuotationWorkflowItemDto;
import org.ever._4ever_be_business.sd.service.DashboardSupplierQuotationService;
import org.ever._4ever_be_business.sd.service.QuotationService;
import org.ever._4ever_be_business.sd.vo.ScmQuotationSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardSupplierQuotationServiceImpl implements DashboardSupplierQuotationService {

    private final QuotationService quotationService;
    private final SupplierCompanyServicePort supplierCompanyServicePort;

    @Override
    public Page<SupplierQuotationWorkflowItemDto> getSupplierQuotationList(SupplierQuotationRequestDto request, Pageable pageable) {
        String userId = request.getUserId();
        log.info("[BUSINESS][SD] 대시보드 공급사 발주서 목록 조회 - userId: {}, page: {}, size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        // TODO: supplierCompanyId 필터링을 Repository 단계에서 지원하도록 확장
        // 우선 기존 SCM용 견적 목록 조회를 활용하고, 후속 단계에서 supplierCompanyId 기반 필터 추가
        ScmQuotationSearchConditionVo condition = new ScmQuotationSearchConditionVo(
                null, null, null, null
        );

        Page<ScmQuotationListItemDto> page = quotationService.getScmQuotationList(condition, pageable);
        return page.map(this::toDashboardItem);
    }

    private SupplierQuotationWorkflowItemDto toDashboardItem(ScmQuotationListItemDto src) {
        return SupplierQuotationWorkflowItemDto.builder()
                .itemId(src.getQuotationId())
                .itemNumber(src.getQuotationNumber())
                .itemTitle(src.getCustomerName())
                .name("")
                .statusCode(src.getStatusCode())
                .date(src.getRequestDate())
                .build();
    }
}

