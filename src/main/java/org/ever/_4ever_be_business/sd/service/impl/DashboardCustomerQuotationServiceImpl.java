package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.hr.repository.CustomerUserRepository;
import org.ever._4ever_be_business.order.entity.Quotation;
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
                .map(quotation -> toDashboardItem(quotation, customerUser.getCustomerName(),
                        customerUser.getCustomerCompany() != null
                                ? customerUser.getCustomerCompany().getCompanyName()
                                : "고객사 미지정"))
                .toList();
    }

    @Override
    public List<DashboardWorkflowItemDto> getAllQuotations(int size) {
        int limit = size > 0 ? size : 5;

        return quotationRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit))
                .stream()
                .map(quotation -> toDashboardItem(
                        quotation,
                        quotation.getCustomerUserId(),
                        quotation.getCustomer() != null ? quotation.getCustomer().getCompanyName() : "고객사 미지정"
                ))
                .toList();
    }

    private DashboardWorkflowItemDto toDashboardItem(Quotation quotation, String requesterName, String companyName) {
        return DashboardWorkflowItemDto.builder()
                .itemId(quotation.getId())
                .itemTitle(companyName)
                .itemNumber(quotation.getQuotationCode())
                .name(requesterName)
                .statusCode(quotation.getQuotationApproval() != null
                        ? quotation.getQuotationApproval().getApprovalStatus()
                        : "PENDING")
                .date(quotation.getCreatedAt() != null
                        ? quotation.getCreatedAt().toLocalDate().format(ISO_FORMATTER)
                        : null)
                .build();
    }
}
