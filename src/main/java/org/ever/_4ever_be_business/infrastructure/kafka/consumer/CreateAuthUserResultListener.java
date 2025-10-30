package org.ever._4ever_be_business.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.async.AsyncResultManager;
import org.ever._4ever_be_business.common.saga.SagaCompensationService;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig;
import org.ever._4ever_be_business.infrastructure.kafka.producer.KafkaProducerService;
import org.ever.event.CreateAuthUserResultEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.hr.user-service.adapter", havingValue = "kafka", matchIfMissing = true)
public class CreateAuthUserResultListener {

    private final AsyncResultManager<CreateAuthUserResultEvent> asyncResultManager;
    private final KafkaProducerService kafkaProducerService;
    private final SagaCompensationService sagaCompensationService;

    @KafkaListener(
            topics = {
                KafkaTopicConfig.AUTH_USER_RESULT_TOPIC,
                KafkaTopicConfig.CUSTOMER_USER_RESULT_TOPIC
            },
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleCreateAuthUserResult(
            CreateAuthUserResultEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        log.info("[KAFKA] 사용자 생성 결과 수신 - topic: {}, partition: {}, offset: {}, transactionId: {}, success: {}",
                topic, partition, offset, event.getTransactionId(), event.isSuccess());

        String transactionId = event.getTransactionId();
        if (transactionId == null) {
            log.error("[KAFKA][ERROR] transactionId 가 없는 결과 이벤트: {}", event);
            acknowledgment.acknowledge();
            return;
        }

        if (!asyncResultManager.hasPendingResult(transactionId)) {
            log.warn("[KAFKA][WARN] 처리할 DeferredResult가 없어 결과 이벤트를 무시합니다. transactionId: {}", transactionId);
            acknowledgment.acknowledge();
            return;
        }

        try {
            if (event.isSuccess()) {
                asyncResultManager.setSuccessResult(
                        transactionId,
                        event,
                        "[SAGA][SUCCESS] 사용자 생성이 완료되었습니다.",
                        HttpStatus.CREATED
                );
                log.info("[KAFKA] 사용자 생성 성공 처리 완료 - transactionId: {}", transactionId);
            } else {
                asyncResultManager.setErrorResult(
                        transactionId,
                        "[SAGA][FAIL] 사용자 생성 실패: " + event.getFailureReason(),
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
//                publishRollbackEvent(event);
                sagaCompensationService.compensate(transactionId);
            }

            acknowledgment.acknowledge();
        } catch (Exception ex) {
            log.error("[KAFKA][ERROR] 내부 사용자 생성 결과 처리 중 오류 - transactionId: {}", transactionId, ex);
            asyncResultManager.setErrorResult(
                    transactionId,
                    "[SAGA][FAIL] 결과 처리 중 오류가 발생했습니다: " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private void publishRollbackEvent(CreateAuthUserResultEvent event) {
        CreateAuthUserResultEvent rollbackEvent = CreateAuthUserResultEvent.builder()
                .eventId(UuidV7Generator.generate())
                .transactionId(event.getTransactionId())
                .success(false)
                .userId(event.getUserId())
                .failureReason(event.getFailureReason())
                .build();

        kafkaProducerService.sendEventSync(
                KafkaTopicConfig.USER_ROLLBACK_TOPIC,
                rollbackEvent.getTransactionId(),
                rollbackEvent
        );
        log.info("[KAFKA] 내부 사용자 생성 실패 보상 이벤트 발행 - transactionId: {}", rollbackEvent.getTransactionId());
    }
}
