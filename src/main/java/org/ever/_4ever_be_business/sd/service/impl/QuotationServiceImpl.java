package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.common.util.CodeGenerator;
import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.order.dao.QuotationDAO;
import org.ever._4ever_be_business.order.entity.*;
import org.ever._4ever_be_business.order.enums.Unit;
import org.ever._4ever_be_business.order.repository.OrderItemRepository;
import org.ever._4ever_be_business.order.repository.OrderRepository;
import org.ever._4ever_be_business.order.repository.QuotationApprovalRepository;
import org.ever._4ever_be_business.order.repository.QuotationItemRepository;
import org.ever._4ever_be_business.voucher.entity.SalesVoucher;
import org.ever._4ever_be_business.voucher.enums.SalesVoucherStatus;
import org.ever._4ever_be_business.voucher.repository.SalesVoucherRepository;
import org.ever._4ever_be_business.sd.dto.request.CreateQuotationRequestDto;
import org.ever._4ever_be_business.sd.dto.request.InventoryCheckRequestDto;
import org.ever._4ever_be_business.sd.dto.request.QuotationItemRequestDto;
import org.ever._4ever_be_business.sd.dto.response.InventoryCheckResponseDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationDetailDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationItemDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationListItemDto;
import org.ever._4ever_be_business.sd.integration.dto.ProductInfoResponseDto;
import org.ever._4ever_be_business.sd.integration.port.InventoryServicePort;
import org.ever._4ever_be_business.sd.integration.port.ProductServicePort;
import org.ever._4ever_be_business.sd.service.QuotationService;
import org.ever._4ever_be_business.sd.vo.QuotationDetailVo;
import org.ever._4ever_be_business.sd.vo.QuotationSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuotationServiceImpl implements QuotationService {

    private final QuotationDAO quotationDAO;
    private final QuotationApprovalRepository quotationApprovalRepository;
    private final QuotationItemRepository quotationItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SalesVoucherRepository salesVoucherRepository;
    private final ProductServicePort productServicePort;
    private final InventoryServicePort inventoryServicePort;
    private final org.ever._4ever_be_business.hr.repository.CustomerUserRepository customerUserRepository;
    private final org.ever._4ever_be_business.company.repository.CustomerCompanyRepository customerCompanyRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional(readOnly = true)
    public QuotationDetailDto getQuotationDetail(QuotationDetailVo vo) {
        log.info("견적 상세 조회 요청 - quotationId: {}", vo.getQuotationId());

        // 1. Quotation 엔티티 조회
        Quotation quotation = quotationDAO.findQuotationEntityById(vo.getQuotationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.QUOTATION_NOT_FOUND));

        // 2. Customer 정보 조회 (CustomerUser → CustomerCompany)
        org.ever._4ever_be_business.hr.entity.CustomerUser customerUser =
                customerUserRepository.findById(quotation.getCustomerUserId())
                        .orElse(null);

        String customerName = "";
        String ceoName = "";
        if (customerUser != null && customerUser.getCustomerCompany() != null) {
            customerName = customerUser.getCustomerCompany().getCompanyName();
            ceoName = customerUser.getCustomerCompany().getCeoName();
        }

        // 3. QuotationItem 목록 조회
        List<QuotationItem> quotationItems = quotationItemRepository.findAll().stream()
                .filter(item -> item.getQuotation().getId().equals(vo.getQuotationId()))
                .collect(Collectors.toList());

        // 4. Product 정보 조회 (Adapter 사용)
        List<String> productIds = quotationItems.stream()
                .map(QuotationItem::getProductId)
                .collect(Collectors.toList());

        Map<String, ProductInfoResponseDto.ProductDto> productMap = Map.of();
        if (!productIds.isEmpty()) {
            ProductInfoResponseDto productInfo = productServicePort.getProductsByIds(productIds);
            productMap = productInfo.getProducts().stream()
                    .collect(Collectors.toMap(
                            ProductInfoResponseDto.ProductDto::getProductId,
                            product -> product
                    ));
        }

        // 5. QuotationItemDto 목록 생성
        final Map<String, ProductInfoResponseDto.ProductDto> finalProductMap = productMap;
        List<QuotationItemDto> items = quotationItems.stream()
                .map(item -> {
                    ProductInfoResponseDto.ProductDto product = finalProductMap.get(item.getProductId());
                    String itemId = product != null ? product.getProductCode() : item.getProductId();
                    String itemName = product != null ? product.getProductName() : "Unknown";

                    // Unit enum 값 그대로 사용 (예: "EA", "KG")
                    String uomName = item.getUnit().name();

                    BigDecimal amount = item.getPrice().multiply(BigDecimal.valueOf(item.getCount()));

                    return new QuotationItemDto(
                            itemId,               // itemId (productCode)
                            itemName,             // itemName
                            item.getCount(),      // quantity
                            uomName,              // uomName (Unit enum 값)
                            item.getPrice(),      // unitPrice
                            amount                // amount
                    );
                })
                .collect(Collectors.toList());

        // 6. 최종 DTO 생성
        QuotationDetailDto result = new QuotationDetailDto(
                quotation.getId(),
                quotation.getQuotationCode(),
                quotation.getCreatedAt().format(DATE_FORMATTER),
                quotation.getDueDate().format(DATE_FORMATTER),
                quotation.getQuotationApproval() != null ?
                        quotation.getQuotationApproval().getApprovalStatus().name() : "PENDING",
                customerName,
                ceoName,
                items,
                quotation.getTotalPrice()
        );

        log.info("견적 상세 조회 성공 - quotationCode: {}, totalAmount: {}",
                result.getQuotationNumber(), result.getTotalAmount());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuotationListItemDto> getQuotationList(QuotationSearchConditionVo condition, Pageable pageable) {
        log.info("견적 목록 조회 요청 - status: {}, startDate: {}, endDate: {}, search: {}, sort: {}",
                condition.getStatus(), condition.getStartDate(), condition.getEndDate(),
                condition.getSearch(), condition.getSort());

        Page<QuotationListItemDto> result = quotationDAO.findQuotationList(condition, pageable);

        log.info("견적 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return result;
    }

    @Override
    @Transactional
    public String createQuotation(CreateQuotationRequestDto dto) {
        log.info("견적서 생성 요청 - userId: {}, dueDate: {}, items count: {}",
                dto.getUserId(), dto.getDueDate(), dto.getItems().size());

        // 1. CustomerUser 조회 (userId로)
        org.ever._4ever_be_business.hr.entity.CustomerUser customerUser = customerUserRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("CustomerUser를 찾을 수 없습니다. userId: " + dto.getUserId()));

        log.info("CustomerUser 조회 성공 - id: {}, name: {}", customerUser.getId(), customerUser.getCustomerName());

        // 2. Product 정보 조회 (Adapter 사용)
        List<String> productIds = dto.getItems().stream()
                .map(QuotationItemRequestDto::getItemId)
                .collect(Collectors.toList());

        ProductInfoResponseDto productInfo = productServicePort.getProductsByIds(productIds);

        Map<String, ProductInfoResponseDto.ProductDto> productMap = productInfo.getProducts().stream()
                .collect(Collectors.toMap(
                        ProductInfoResponseDto.ProductDto::getProductId,
                        product -> product
                ));

        // 3. 총액 계산
        BigDecimal totalAmount = dto.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. QuotationApproval 생성 (초기 상태: PENDING)
        QuotationApproval approval = QuotationApproval.createPending();
        QuotationApproval savedApproval = quotationApprovalRepository.save(approval);

        // 5. Quotation 생성 (CustomerUser의 id를 customerUserId에 저장)
        String quotationCode = CodeGenerator.generateCode("QO");
        LocalDate dueDate = LocalDate.parse(dto.getDueDate(), DATE_FORMATTER);

        Quotation quotation = new Quotation(
                quotationCode,
                customerUser.getId(),  // CustomerUser의 PK(id)를 저장
                totalAmount,
                savedApproval,
                dueDate.atStartOfDay(),
                dto.getNote()
        );

        Quotation savedQuotation = quotationDAO.saveQuotation(quotation);

        // 5. QuotationItem들 생성
        List<QuotationItem> quotationItems = dto.getItems().stream()
                .map(itemDto -> {
                    // Product 정보에서 Unit 가져오기 (기본값: EA)
                    Unit unit = Unit.EA;  // TODO: Product에서 실제 단위 정보 가져오기

                    return new QuotationItem(
                            savedQuotation,
                            itemDto.getItemId(),
                            itemDto.getQuantity(),
                            unit,
                            itemDto.getUnitPrice()
                    );
                })
                .collect(Collectors.toList());

        quotationItemRepository.saveAll(quotationItems);

        log.info("견적서 생성 성공 - quotationId: {}, quotationCode: {}, totalAmount: {}",
                savedQuotation.getId(), quotationCode, totalAmount);

        return savedQuotation.getId();
    }

    @Override
    @Transactional
    public void approveQuotation(String quotationId, String employeeId) {
        log.info("견적서 승인 및 주문 생성 요청 - quotationId: {}, employeeId: {}", quotationId, employeeId);

        // 1. Quotation 엔티티 조회
        Quotation quotation = quotationDAO.findQuotationEntityById(quotationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUOTATION_NOT_FOUND));

        // 2. QuotationApproval 조회 및 상태 변경
        QuotationApproval approval = quotation.getQuotationApproval();
        if (approval == null) {
            throw new BusinessException(ErrorCode.QUOTATION_APPROVAL_NOT_FOUND);
        }
        approval.approveAndReadyForShipment(employeeId);
        quotationApprovalRepository.save(approval);
        log.info("견적서 승인 완료 - status: APPROVAL");

        // 3. QuotationItem 목록 조회
        List<QuotationItem> quotationItems = quotationItemRepository.findAll().stream()
                .filter(item -> item.getQuotation().getId().equals(quotationId))
                .collect(Collectors.toList());

        // 4. CustomerCompany 조회
        org.ever._4ever_be_business.hr.entity.CustomerUser customerUser =
                customerUserRepository.findById(quotation.getCustomerUserId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));
        CustomerCompany customerCompany = customerUser.getCustomerCompany();
        if (customerCompany == null) {
            throw new BusinessException(ErrorCode.CUSTOMER_COMPANY_NOT_FOUND);
        }

        // 5. Order 생성
        String orderCode = CodeGenerator.generateCode("OR");
        Order order = new Order(
                orderCode,
                quotation,
                null,  // OrderShipment는 나중에 생성
                quotation.getCustomerUserId(),
                quotation.getTotalPrice(),
                LocalDateTime.now(),  // orderDate
                quotation.getDueDate(),
                OrderStatus.READY_FOR_SHIPMENT
        );
        orderRepository.save(order);
        log.info("Order 생성 완료 - orderCode: {}", orderCode);

        // 7. OrderItem 생성
        for (QuotationItem quotationItem : quotationItems) {
            OrderItem orderItem = new OrderItem(
                    order,
                    quotationItem.getProductId(),
                    quotationItem.getCount(),
                    quotationItem.getUnit(),
                    quotationItem.getPrice().longValue()
            );
            orderItemRepository.save(orderItem);
        }
        log.info("OrderItem 생성 완료 - count: {}", quotationItems.size());

        // 8. SalesVoucher 생성
        String voucherCode = CodeGenerator.generateCode("SV");
        SalesVoucher salesVoucher = new SalesVoucher(
                customerCompany,
                order,
                voucherCode,
                LocalDateTime.now(),  // issueDate
                quotation.getDueDate(),  // dueDate
                quotation.getTotalPrice(),
                SalesVoucherStatus.PENDING,
                "견적서 승인을 통한 자동 생성"
        );
        salesVoucherRepository.save(salesVoucher);
        log.info("SalesVoucher 생성 완료 - voucherCode: {}", voucherCode);

        log.info("견적서 승인 및 주문 생성 완료 - quotationId: {}, orderId: {}", quotationId, order.getId());
    }

    @Override
    @Transactional
    public void confirmQuotation(String quotationId) {
        log.info("견적서 검토 확정 요청 - quotationId: {}", quotationId);

        // 1. Quotation 엔티티 조회
        Quotation quotation = quotationDAO.findQuotationEntityById(quotationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUOTATION_NOT_FOUND));

        // 2. QuotationApproval 조회
        QuotationApproval approval = quotation.getQuotationApproval();
        if (approval == null) {
            throw new BusinessException(ErrorCode.QUOTATION_APPROVAL_NOT_FOUND);
        }

        // 3. 검토 확정 처리
        approval.review();
        quotationApprovalRepository.save(approval);

        log.info("견적서 검토 확정 성공 - quotationId: {}, status: {}",
                quotationId, approval.getApprovalStatus());
    }

    @Override
    @Transactional
    public void rejectQuotation(String quotationId, String reason) {
        log.info("견적서 거부 요청 - quotationId: {}, reason: {}", quotationId, reason);

        // 1. Quotation 엔티티 조회
        Quotation quotation = quotationDAO.findQuotationEntityById(quotationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUOTATION_NOT_FOUND));

        // 2. QuotationApproval 조회
        QuotationApproval approval = quotation.getQuotationApproval();
        if (approval == null) {
            throw new BusinessException(ErrorCode.QUOTATION_APPROVAL_NOT_FOUND);
        }

        // 3. 거부 처리
        approval.reject(reason);
        quotationApprovalRepository.save(approval);

        log.info("견적서 거부 완료 - quotationId: {}, status: REJECTED", quotationId);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryCheckResponseDto checkInventory(InventoryCheckRequestDto requestDto) {
        log.info("재고 확인 요청 - items count: {}", requestDto.getItems().size());

        // SCM Inventory 서비스 호출 (단순 프록시 역할)
        InventoryCheckResponseDto response = inventoryServicePort.checkInventory(requestDto);

        log.info("재고 확인 성공 - items count: {}", response.getItems().size());

        return response;
    }
}
