package org.ever._4ever_be_business.msapractice.service;

import jakarta.annotation.PostConstruct;
import org.ever._4ever_be_business.common.async.AsyncResultManager;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.common.response.ApiResponse;
import org.ever._4ever_be_business.common.saga.CompensationHandler;
import org.ever._4ever_be_business.common.saga.SagaCompensationService;
import org.ever._4ever_be_business.common.saga.SagaTransactionManager;
import org.ever.event.CreateUserEvent;
import org.ever._4ever_be_business.infrastructure.kafka.producer.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.msapractice.dto.CreateCustomerRequestDto;
import org.ever._4ever_be_business.msapractice.dto.CustomerResponseDto;
import org.ever._4ever_be_business.msapractice.entity.CustomerCompany;
import org.ever._4ever_be_business.msapractice.repository.CustomerCompanyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerCompanyRepository customerCompanyRepository;
    private final KafkaProducerService kafkaProducerService;
    private final SagaTransactionManager sagaManager;
    private final SagaCompensationService compensationService;
    private final AsyncResultManager<Void> asyncResultManager;

    @PostConstruct
    public void init() {
        // 고객 회사 정보에 대한 보상 핸들러 등록
        compensationService.registerCompensationHandler("customerCompany", new CompensationHandler() {
            @Override
            public void restore(Object entity) {
                if (entity instanceof CustomerCompany) {
                    CustomerCompany company = (CustomerCompany) entity;
                    log.info("고객 회사 정보 복원: id={}, customerUserId={}", company.getId(), company.getCustomerUserId());
                    customerCompanyRepository.save(company);
                }
            }
            
            @Override
            public void delete(String entityId) {
                log.info("고객 회사 정보 삭제: id={}", entityId);
                customerCompanyRepository.findById(Long.valueOf(entityId))
                    .ifPresent(customerCompanyRepository::delete);
            }
            
            @Override
            public Class<?> getEntityClass() {
                return CustomerCompany.class;
            }
        });
    }

    @Override
    public void createCustomerAsync(CreateCustomerRequestDto requestDto,
            DeferredResult<ResponseEntity<ApiResponse<Void>>> deferredResult) {
        // 트랜잭션 ID 생성
        String transactionId = UUID.randomUUID().toString();
        
        // 트랜잭션 ID로 DeferredResult 등록
        asyncResultManager.registerResult(transactionId, deferredResult);
        
        // Saga 트랜잭션 실행 (외부에서 생성한 트랜잭션 ID 사용)
        sagaManager.executeSagaWithId(transactionId, () -> {
            try {
                // 중복 체크
                if (customerCompanyRepository.findByBusinessNumber(requestDto.getBusinessNumber()).isPresent()) {
                    throw new BusinessException(ErrorCode.DATABASE_ERROR, "이미 등록된 사업자 번호입니다.");
                }

                // UUID 생성
                String customerUserId = UUID.randomUUID().toString();

                // 회사 코드 생성 (간단하게 처리)
                String companyCode = "C" + System.currentTimeMillis() % 10000;

                // CustomerCompany 엔티티 생성 및 저장
                CustomerCompany customerCompany = CustomerCompany.builder()
                        .customerUserId(customerUserId)
                        .companyCode(companyCode)
                        .companyName(requestDto.getCompanyName())
                        .businessNumber(requestDto.getBusinessNumber())
                        .ceoName(requestDto.getCeoName())
                        .officePhone(requestDto.getContactPhone())
                        .officeEmail(requestDto.getContactEmail())
                        .baseAddress(requestDto.getAddress())
                        .etc(requestDto.getNote())
                        .build();

                CustomerCompany savedCompany = customerCompanyRepository.save(customerCompany);
                log.info("고객 회사 정보 저장 완료: id={}, customerUserId={}", savedCompany.getId(), customerUserId);

                // Kafka 이벤트 발행 (User 서버로 사용자 생성 요청)
                CreateUserEvent event = CreateUserEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .customerUserId(customerUserId)
                        .transactionId(transactionId)
                        .companyName(savedCompany.getCompanyName())
                        .businessNumber(savedCompany.getBusinessNumber())
                        .managerName(requestDto.getManager().getName())
                        .managerEmail(requestDto.getManager().getEmail())
                        .managerMobile(requestDto.getManager().getMobile())
                        .build();

                kafkaProducerService.sendCreateUserEvent(event);
                log.info("사용자 생성 요청 이벤트 발행: eventId={}, customerUserId={}", event.getEventId(), customerUserId);

                return CustomerResponseDto.builder()
                        .id(savedCompany.getId())
                        .customerUserCode(customerUserId)
                        .companyName(savedCompany.getCompanyName())
                        .businessNumber(savedCompany.getBusinessNumber())
                        .ceoName(savedCompany.getCeoName())
                        .contactPhone(savedCompany.getOfficePhone())
                        .contactEmail(savedCompany.getOfficeEmail())
                        .address(savedCompany.getBaseAddress())
                        .manager(requestDto.getManager())
                        .note(savedCompany.getEtc())
                        .status("PENDING")  // 사용자 생성이 완료될 때까지 PENDING 상태
                        .build();

            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                log.error("고객 정보 저장 중 오류 발생: {}", e.getMessage(), e);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                        "고객 정보 저장 중 시스템 오류가 발생했습니다");
            }
        });
    }

    @Override
    public boolean processServiceCompletionEvent(String customerUserId, boolean success, String transactionId) {
        return sagaManager.executeSagaWithId(transactionId, () -> {
            try {
                log.info("서비스 완료 이벤트 수신: customerUserId={}, success={}", customerUserId, success);

                // 서비스 완료 이벤트 처리 (필요하다면 customerCompany 상태 업데이트 등)

                return true;
            } catch (Exception e) {
                log.error("서비스 완료 이벤트 처리 중 오류: customerUserId={}, {}", customerUserId, e.getMessage(), e);
                return false;
            }
        });
    }
}