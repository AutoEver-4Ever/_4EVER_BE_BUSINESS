package org.ever._4ever_be_business.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.saga.SagaTransactionManager;
import org.ever._4ever_be_business.infrastructure.kafka.producer.KafkaProducerService;
import org.ever._4ever_be_business.order.entity.Order;
import org.ever._4ever_be_business.order.entity.OrderStatus;
import org.ever._4ever_be_business.order.repository.OrderRepository;
import org.ever.event.SalesOrderStatusChangeEvent;
import org.ever.event.SalesOrderStatusChangeCompletionEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import static org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig.SALES_ORDER_STATUS_CHANGE_COMPLETION_TOPIC;
import static org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig.SALES_ORDER_STATUS_CHANGE_TOPIC;

/**
 * 판매주문 상태 변경 이벤트 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SalesOrderStatusChangeListener {

    private final SagaTransactionManager sagaTransactionManager;
    private final OrderRepository orderRepository;
    private final KafkaProducerService kafkaProducerService;

    @KafkaListener(topics = SALES_ORDER_STATUS_CHANGE_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handleSalesOrderStatusChange(SalesOrderStatusChangeEvent event, Acknowledgment acknowledgment) {
        log.info("판매주문 상태 변경 이벤트 수신: transactionId={}, salesOrderId={}, itemIds={}",
                event.getTransactionId(), event.getSalesOrderId(), event.getItemIds());

        try {
            // Saga 트랜잭션으로 실행
            sagaTransactionManager.executeSagaWithId(event.getTransactionId(), () -> {
                // 1. SalesOrderId로 Order 조회
                Order order = orderRepository.findById(event.getSalesOrderId()).orElseThrow(() ->
                        new RuntimeException("Order not found: " + event.getSalesOrderId()));

                // 2. Order 상태를 DELIVERING으로 변경
                order.setStatus(OrderStatus.DELIVERING);
                orderRepository.save(order);

                log.info("Order 상태 업데이트 완료: orderId={}, status=DELIVERING", order.getId());

                return null;
            });

            // 3. 완료 이벤트 발송
            SalesOrderStatusChangeCompletionEvent completionEvent = SalesOrderStatusChangeCompletionEvent.builder()
                    .transactionId(event.getTransactionId())
                    .salesOrderId(event.getSalesOrderId())
                    .itemIds(event.getItemIds())
                    .success(true)
                    .timestamp(System.currentTimeMillis())
                    .build();

            kafkaProducerService.sendToTopic(SALES_ORDER_STATUS_CHANGE_COMPLETION_TOPIC,
                    event.getSalesOrderId(), completionEvent);

            log.info("판매주문 상태 변경 완료 이벤트 발송: transactionId={}", event.getTransactionId());

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("판매주문 상태 변경 처리 실패: transactionId={}, salesOrderId={}",
                    event.getTransactionId(), event.getSalesOrderId(), e);

            // 실패 이벤트 발송
            SalesOrderStatusChangeCompletionEvent completionEvent = SalesOrderStatusChangeCompletionEvent.builder()
                    .transactionId(event.getTransactionId())
                    .salesOrderId(event.getSalesOrderId())
                    .itemIds(event.getItemIds())
                    .success(false)
                    .errorMessage(e.getMessage())
                    .timestamp(System.currentTimeMillis())
                    .build();

            kafkaProducerService.sendToTopic(SALES_ORDER_STATUS_CHANGE_COMPLETION_TOPIC,
                    event.getSalesOrderId(), completionEvent);

            acknowledgment.acknowledge();
        }
    }
}
