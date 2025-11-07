package org.ever._4ever_be_business.sd.service.impl;

package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.hr.repository.CustomerUserRepository;
import org.ever._4ever_be_business.order.repository.QuotationRepository;
import org.ever._4ever_be_business.sd.dto.response.DashboardWorkflowItemDto;
import org.ever._4ever_be_business.sd.service.DashboardCustomerQuotationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardCustomerQuotationServiceImpl implements DashboardCustomerQuotationService {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final CustomerUserRepository customerUserRepository;
    private final QuotationRepository quotationRepository;

    @Override
    public List<DashboardWorkflowItemDto> getCustomerQuotations(String userId, int size) {
        int limit = size > 0 ? size : 5;

        CustomerUser customerUser = customerUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

        var page = quotationRepository.findByCustomerUserIdOrderByCreatedAtDesc(
                customerUser.getId(),
                PageRequest.of(0, limit)
        );

        return page.stream()
                .map(quotation -> DashboardWorkflowItemDto.builder()
                        .itemId(quotation.getId())
                        .itemTitle(customerUser.getCustomerCompany() != null
                                ? customerUser.getCustomerCompany().getCompanyName()
                                : "고객사 미지정")
                        .itemNumber(quotation.getQuotationCode())
                        .name(customerUser.getCustomerName())
                        .statusCode(quotation.getQuotationApproval() != null
                                ? quotation.getQuotationApproval().getApprovalStatus()
                                : "PENDING")
                        .date(quotation.getCreatedAt() != null
                                ? quotation.getCreatedAt().toLocalDate().format(ISO_FORMATTER)
                                : null)
                        .build())
                .toList();
    }
}
