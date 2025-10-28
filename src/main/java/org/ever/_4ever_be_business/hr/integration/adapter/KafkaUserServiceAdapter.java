package org.ever._4ever_be_business.hr.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.hr.dto.request.AuthUserCreateRequestDto;
import org.ever._4ever_be_business.hr.dto.response.UserInfoResponse;
import org.ever._4ever_be_business.hr.integration.port.UserServicePort;
import org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig;
import org.ever._4ever_be_business.infrastructure.kafka.producer.KafkaProducerService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

// 사가 코레오그래피 발화자로 Auth 서버에 내부 사용자 생성을 요청하는 이벤트를
// 카프카로 발행하여 사가 흐름을 시작함.
// 분산 트랜잭션을 직접 조정(오케스트레이션)하지 않고, 자신의 단계(발행)만 수행함.

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.mock.enabled", havingValue = "kafka", matchIfMissing = true)
public class KafkaUserServiceAdapter implements UserServicePort {

    private final KafkaProducerService kafkaProducerService;

    @Override
    public CompletableFuture<UserInfoResponse> getMultipleUserInfo(List<Long> internelUserIds) {
        throw new UnsupportedOperationException("현재 미지원 카프카 메서드: getMultipleUserInfo");
    }

    // CompletableFuture: 비동기 프로그래밍을 지원하는 클래스임.
    // - 코레오그래피에서는 발생 성공(브로커 저장)만 보장하면 충분함.
    // - 최종 성공/실패는 완료 이벤트(process-completed)에서 확정함.
    @Override
    public CompletableFuture<Void> createInternalUserAccount(
            AuthUserCreateRequestDto requestDto
    ) {
        try {
            String eventId = UuidV7Generator.generate();        // 이벤트 키
            String transactionId = UuidV7Generator.generate();  // 상관 키
            String key = requestDto.getUserId();                // 파티션 키

            // 동기 발행(sendEventSync)로 카프카 메시지 발생
            kafkaProducerService.sendEventSync(
                    KafkaTopicConfig.CREATE_USER_TOPIC,
                    key,
                    requestDto
            );
            log.info("[KAFKA] 내부 사용자 생성 요청 발행 완료 - eventId: {}, transactionId: {}, key: {}, email: {}",
                    eventId, transactionId, key, requestDto.getUserEmail());

            return CompletableFuture.completedFuture(null);
        } catch (Exception error) {
            log.error("[KAFKA] 내부 사용자 생성 요청 발행 실패 - userId: {}, error: {}", requestDto.getUserId(), error.getMessage());
            throw new RuntimeException("사용자 계정 생성 요청 발행 실패", error);
        }
    }
}
