package org.ever._4ever_be_business.fcm.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.mock.MockDataProvider;
import org.ever._4ever_be_business.fcm.integration.dto.OrderItemsResponseDto;
import org.ever._4ever_be_business.fcm.integration.port.OrderServicePort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * OrderServicePort의 Mock 구현체
 * dev 환경에서 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockOrderServiceAdapter implements OrderServicePort {

    private final MockDataProvider mockDataProvider;

    @Override
    public OrderItemsResponseDto getOrderItemsById(String orderId) {
        log.info("[MOCK ADAPTER] getOrderItemsById 호출 - orderId: {}", orderId);
        return mockDataProvider.createMockOrderItems(orderId);
    }
}
