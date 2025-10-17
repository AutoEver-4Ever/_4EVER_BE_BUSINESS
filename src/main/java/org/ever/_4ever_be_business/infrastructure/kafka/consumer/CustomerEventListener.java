package org.ever._4ever_be_business.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.saga.SagaCompensationService;
import org.ever.event.ProcessCompletedEvent;
import org.ever._4ever_be_business.common.async.AsyncResultManager;
import org.ever._4ever_be_business.msapractice.service.CustomerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import static org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig.*;
/**
 * User 서버와 Alarm 서버로부터의 Kafka 메시지를 처리하는 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventListener {

    private final CustomerService customerService;
    private final AsyncResultManager<Void> asyncResultManager;
    private final SagaCompensationService sagaCompensationService;

    /**
     * Alarm 서버로부터 서비스 완료 이벤트를 받아서 처리
     *
     * @param event Kafka 메시지 (JSON 형태)
     */
    @KafkaListener(
            topics = PROCESS_COMPLETED_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleServiceCompletion(ProcessCompletedEvent event) {
        try {
            log.info("서비스 완료 이벤트 수신: {}", event);

            String customerUserId = event.getCustomerUserId();
            boolean success = event.isSuccess();

            boolean processed = customerService.processServiceCompletionEvent(customerUserId, success,event.getTransactionId());

            if (processed) {
                log.info("서비스 완료 이벤트 처리 완료: customerUserId={}, success={}", customerUserId, success);
                asyncResultManager.setSuccessResult(event.getTransactionId(),null);
            } else {
                log.warn("서비스 완료 이벤트 처리 실패: customerUserId={}", customerUserId);
            }
        } catch (Exception e) {
            log.error("서비스 완료 이벤트 처리 중 오류: {}", e.getMessage(), e);
            sagaCompensationService.compensate(event.getTransactionId());
        }
    }
}
