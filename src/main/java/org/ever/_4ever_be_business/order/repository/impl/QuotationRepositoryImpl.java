package org.ever._4ever_be_business.order.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.order.enums.ApprovalStatus;
import org.ever._4ever_be_business.order.repository.QuotationRepositoryCustom;
import org.ever._4ever_be_business.sd.dto.response.QuotationDetailDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationItemDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationListItemDto;
import org.ever._4ever_be_business.sd.vo.QuotationSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.ever._4ever_be_business.order.entity.QQuotation.quotation;
import static org.ever._4ever_be_business.order.entity.QQuotationItem.quotationItem;
import static org.ever._4ever_be_business.order.entity.QQuotationApproval.quotationApproval;
import static org.ever._4ever_be_business.hr.entity.QCustomerUser.customerUser;
import static org.ever._4ever_be_business.company.entity.QCustomerCompany.customerCompany;

@Repository
@RequiredArgsConstructor
public class QuotationRepositoryImpl implements QuotationRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Optional<QuotationDetailDto> findQuotationDetailById(String quotationId) {
        // Repository는 기본 정보만 반환, Product 정보와 Customer 정보는 Service에서 채움
        // 빈 DTO를 반환하고 Service에서 채우도록 변경
        // 실제로는 Quotation 엔티티를 조회하는 것이 더 나을 수 있음

        // Quotation이 존재하는지만 확인
        Long count = queryFactory
                .select(quotation.count())
                .from(quotation)
                .where(quotation.id.eq(quotationId))
                .fetchOne();

        if (count == null || count == 0) {
            return Optional.empty();
        }

        // 빈 DTO 반환 (Service에서 채울 예정)
        return Optional.of(new QuotationDetailDto(
                null, null, null, null, null, null, null, null, null
        ));
    }

    @Override
    public Page<QuotationListItemDto> findQuotationList(QuotationSearchConditionVo condition, Pageable pageable) {
        // 1. 동적 쿼리 조건 생성
        BooleanBuilder builder = new BooleanBuilder();

        // 견적 ID 필터
        if (condition.getQuotationId() != null && !condition.getQuotationId().isEmpty()) {
            builder.and(quotation.id.eq(condition.getQuotationId()));
        }

        // 날짜 범위 필터 (startDate ~ endDate)
        if (condition.getStartDate() != null && !condition.getStartDate().isEmpty()) {
            java.time.LocalDate startDate = java.time.LocalDate.parse(condition.getStartDate(), DATE_FORMATTER);
            builder.and(quotation.createdAt.goe(startDate.atStartOfDay()));
        }
        if (condition.getEndDate() != null && !condition.getEndDate().isEmpty()) {
            java.time.LocalDate endDate = java.time.LocalDate.parse(condition.getEndDate(), DATE_FORMATTER);
            builder.and(quotation.createdAt.loe(endDate.atTime(23, 59, 59)));
        }

        // 상태 필터
        if (condition.getStatus() != null && !condition.getStatus().equalsIgnoreCase("ALL")) {
            try {
                ApprovalStatus status = ApprovalStatus.valueOf(condition.getStatus());
                builder.and(quotationApproval.approvalStatus.eq(status));
            } catch (IllegalArgumentException e) {
                // 잘못된 상태값은 무시
            }
        }

        // 검색 조건 (type과 search 모두 있을 때만 검색)
        if (condition.getType() != null && !condition.getType().isEmpty() &&
                condition.getSearch() != null && !condition.getSearch().isEmpty()) {

            String search = "%" + condition.getSearch().trim() + "%";

            switch (condition.getType().toLowerCase()) {
                case "quotationnumber" -> builder.and(quotation.quotationCode.like(search));
                case "customername" -> builder.and(customerCompany.companyName.like(search));
                case "managername" -> builder.and(customerUser.customerName.like(search));
            }
        }

        // 2. 정렬 조건
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(condition.getSort());

        // 3. 데이터 조회 (첫 번째 QuotationItem 정보 포함)
        JPAQuery<QuotationListItemDto> query = queryFactory
                .select(Projections.constructor(
                        QuotationListItemDto.class,
                        quotation.id,                              // quotationId
                        quotation.quotationCode,                   // quotationNumber
                        customerCompany.companyName,               // customerName
                        quotationItem.productId,                   // productId (첫 번째 아이템)
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD')",
                                quotation.dueDate
                        ),                                         // dueDate
                        quotationItem.count,                       // quantity (첫 번째 아이템)
                        quotationItem.unit.stringValue(),          // uomName (첫 번째 아이템)
                        quotationApproval.approvalStatus.stringValue()  // statusCode
                ))
                .from(quotation)
                .leftJoin(quotation.quotationApproval, quotationApproval)
                .leftJoin(customerUser).on(customerUser.id.eq(quotation.customerUserId))
                .leftJoin(customerUser.customerCompany, customerCompany)
                .leftJoin(quotationItem).on(quotationItem.quotation.id.eq(quotation.id)
                        .and(quotationItem.id.eq(
                                queryFactory.select(quotationItem.id.min())
                                        .from(quotationItem)
                                        .where(quotationItem.quotation.id.eq(quotation.id))
                        )))
                .where(builder)
                .orderBy(orderSpecifier);

        // 4. 페이징 적용
        List<QuotationListItemDto> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 5. 전체 개수 조회
        Long total = queryFactory
                .select(quotation.count())
                .from(quotation)
                .leftJoin(quotation.quotationApproval, quotationApproval)
                .leftJoin(customerUser).on(customerUser.id.eq(quotation.customerUserId))
                .leftJoin(customerUser.customerCompany, customerCompany)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort) {
        if (sort == null || sort.isEmpty()) {
            return quotation.createdAt.desc(); // 기본값: 견적일자 내림차순
        }

        return switch (sort.toLowerCase()) {
            case "asc" -> quotation.createdAt.asc();
            case "desc" -> quotation.createdAt.desc();
            default -> quotation.createdAt.desc();
        };
    }

    private String mapStatusLabel(String statusCode) {
        if (statusCode == null) {
            return "대기";
        }

        return switch (statusCode) {
            case "PENDING" -> "대기";
            case "APPROVED" -> "승인";
            case "REJECTED" -> "거부";
            case "REVIEW" -> "검토중";
            default -> "대기";
        };
    }
}
