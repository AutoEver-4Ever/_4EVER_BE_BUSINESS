package org.ever._4ever_be_business.fcm.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.fcm.integration.dto.OrderItemsResponseDto;
import org.ever._4ever_be_business.fcm.integration.port.OrderServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * SCM 서버의 Order 서비스와 통신하는 Adapter
 * prod 환경에서 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "external.mock.enabled", havingValue = "false")
public class ScmOrderServiceAdapter implements OrderServicePort {

    private final RestClient restClient;

    @Value("${external.scm.url}")
    private String scmServiceUrl;

    @Override
    public OrderItemsResponseDto getOrderItemsById(String orderId) {
        log.info("SCM Order 아이템 서비스 호출 - orderId: {}", orderId);

        try {
            Map<String, String> requestBody = Map.of("orderId", orderId);

            ApiResponse<OrderItemsResponseDto> response = restClient.post()
                    .uri(scmServiceUrl + "/scm-order/order/items")
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<OrderItemsResponseDto>>() {});

            if (response != null && response.isSuccess()) {
                log.info("SCM Order 아이템 서비스 호출 성공 - orderId: {}", orderId);
                return response.getData();
            } else {
                log.error("SCM Order 아이템 서비스 응답 실패 - response: {}", response);
                throw new RuntimeException("Failed to retrieve order items from SCM service");
            }
        } catch (Exception e) {
            log.error("SCM Order 아이템 서비스 호출 중 오류 발생", e);
            throw new RuntimeException("Error calling SCM Order items service", e);
        }
    }
}
