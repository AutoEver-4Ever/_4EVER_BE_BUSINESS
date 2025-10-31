package org.ever._4ever_be_business.order.repository;

import org.ever._4ever_be_business.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    /**
     * 주문 ID로 주문 항목 목록 조회
     *
     * @param orderId 주문 ID
     * @return 주문 항목 목록
     */
    List<OrderItem> findByOrderId(String orderId);
}
