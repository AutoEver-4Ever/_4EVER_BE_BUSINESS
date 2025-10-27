package org.ever._4ever_be_business.fcm.integration.port;

import org.ever._4ever_be_business.fcm.integration.dto.OrderItemsResponseDto;

/**
 * SCM 서버의 Order 서비스와 통신하기 위한 Port 인터페이스
 */
public interface OrderServicePort {
    /**
     * Order ID로 Order 아이템 정보 조회
     *
     * @param orderId Order ID
     * @return Order 아이템 정보
     */
    OrderItemsResponseDto getOrderItemsById(String orderId);
}
