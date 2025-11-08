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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardCustomerQuotationServiceImpl implements DashboardCustomerQuotationService {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final CustomerUserRepository customerUserRepository;
    private final QuotationRepository quotationRepository;

    @Override
    public List<DashboardWorkflowItemDto> getCustomerQuotations(String userId, int size) {
        int limit = size > 0 ? Math.min(size, 20) : 5;

        CustomerUser customerUser = customerUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

        var page = quotationRepository.findByCustomerUserIdOrderByCreatedAtDesc(
                customerUser.getId(),
                PageRequest.of(0, limit)
        );

        List<Quotation> quotations = page.getContent();
        String requesterName = customerUser.getCustomerName() != null
                ? customerUser.getCustomerName()
                : "고객 담당자";
        String companyName = customerUser.getCustomerCompany() != null
                ? customerUser.getCustomerCompany().getCompanyName()
                : "고객사 미지정";

        if (quotations == null || quotations.isEmpty()) {
            log.info("[DASHBOARD][MOCK] 고객사 견적서 목업 데이터 반환 - userId: {}", userId);
            return buildMockCustomerQuotations(limit, requesterName, companyName);
        }

        return quotations.stream()
                .map(quotation -> toDashboardItem(quotation, customerUser.getCustomerName(),
                        customerUser.getCustomerCompany() != null
                                ? customerUser.getCustomerCompany().getCompanyName()
                                : "고객사 미지정"))
                .toList();
    }

    @Override
    public List<DashboardWorkflowItemDto> getAllQuotations(int size) {
        int limit = size > 0 ? Math.min(size, 20) : 5;

        List<DashboardWorkflowItemDto> items = quotationRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit))
                .stream()
                .map(quotation -> toDashboardItem(
                        quotation,
                        quotation.getCustomerUserId(),
                        quotation.getCustomerUserId()
                ))
                .toList();

        if (items.isEmpty()) {
            log.info("[DASHBOARD][MOCK][SD][QT] 실데이터 없음 - 내부 견적서 목업 데이터 반환");
            return buildMockInternalQuotations(limit);
        }

        return items;
    }

    private DashboardWorkflowItemDto toDashboardItem(Quotation quotation, String requesterName, String companyName) {
        return DashboardWorkflowItemDto.builder()
                .itemId(quotation.getId())
                .itemTitle(companyName)
                .itemNumber(quotation.getQuotationCode())
                .name(requesterName)
                .statusCode(quotation.getQuotationApproval() != null
                        ? quotation.getQuotationApproval().getApprovalStatus().name()
                        : "PENDING")
                .date(quotation.getCreatedAt() != null
                        ? quotation.getCreatedAt().toLocalDate().format(ISO_FORMATTER)
                        : null)
                .build();
    }

    private List<DashboardWorkflowItemDto> buildMockCustomerQuotations(int size, String requesterName, String companyName) {
        int limit = size > 0 ? Math.min(size, 20) : 5;
        int itemCount = Math.min(limit, 5);

        return IntStream.range(0, itemCount)
                .mapToObj(i -> DashboardWorkflowItemDto.builder()
                        .itemId(UUID.randomUUID().toString())
                        .itemTitle(companyName)
                        .itemNumber(String.format("QT-MOCK-%04d", i + 1))
                        .name(requesterName)
                        .statusCode(i % 2 == 0 ? "PENDING" : "APPROVED")
                        .date(OffsetDateTime.now().minusDays(i).toLocalDate().format(ISO_FORMATTER))
                        .build())
                .toList();
    }

    private List<DashboardWorkflowItemDto> buildMockInternalQuotations(int size) {
        int limit = size > 0 ? Math.min(size, 20) : 5;
        int itemCount = Math.min(limit, 5);

        return IntStream.range(0, itemCount)
                .mapToObj(i -> DashboardWorkflowItemDto.builder()
                        .itemId(UUID.randomUUID().toString())
                        .itemTitle("내부 견적 요청 " + (i + 1))
                        .itemNumber(String.format("QT-MOCK-%04d", i + 1))
                        .name("영업 담당자 " + (i + 1))
                        .statusCode(i % 2 == 0 ? "IN_REVIEW" : "APPROVED")
                        .date(LocalDate.now().minusDays(i).toString())
                        .build())
                .toList();
    }
}
