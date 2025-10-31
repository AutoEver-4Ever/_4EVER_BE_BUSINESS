package org.ever._4ever_be_business.order.entity;

public enum OrderStatus {
    PENDING,              // 대기
    CONFIRMED,            // 확정
    IN_PROGRESS,          // 진행중
    READY_FOR_SHIPMENT,   // 출고 준비
    SHIPPED,              // 출고 완료
    DELIVERED,            // 배송 완료
    CANCELLED             // 취소
}
