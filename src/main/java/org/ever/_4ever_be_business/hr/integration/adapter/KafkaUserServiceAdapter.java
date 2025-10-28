package org.ever._4ever_be_business.hr.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.ever._4ever_be_business.hr.dto.request.AuthUserCreateRequestDto;
import org.ever._4ever_be_business.hr.dto.request.AuthUserCreateResponseDto;
import org.ever._4ever_be_business.hr.dto.response.UserInfoResponse;
import org.ever._4ever_be_business.hr.integration.port.UserServicePort;
import org.ever._4ever_be_business.infrastructure.kafka.producer.KafkaProducerService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.mock.enabled", havingValue = "kafka", matchIfMissing = true)
public class KafkaUserServiceAdapter implements UserServicePort {

    private final KafkaProducerService kafkaProducerService;

    @Override
    public CompletableFuture<UserInfoResponse> getMultipleUserInfo(List<Long> internelUserIds) {
        return null;
    }

    @Override
    public CompletableFuture<AuthUserCreateResponseDto> createInternalUserAccount(AuthUserCreateRequestDto request) {
        return null;
    }
}
