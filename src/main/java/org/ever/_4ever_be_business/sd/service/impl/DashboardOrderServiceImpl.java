package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.order.entity.Order;
import org.ever._4ever_be_business.order.repository.OrderRepository;
import org.ever._4ever_be_business.sd.dto.response.DashboardWorkflowItemDto;
import org.ever._4ever_be_business.sd.service.DashboardOrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardOrderServiceImpl implements DashboardOrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<DashboardWorkflowItemDto> getAllOrders(int size) {
        int limit = size > 0 ? size : 5;

        return orderRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit))
                .stream()
                .map(this::toDashboardItem)
                .toList();
    }

    private DashboardWorkflowItemDto toDashboardItem(Order order) {
        return DashboardWorkflowItemDto.builder()
                .itemId(order.getId())
                .itemTitle(order.getCustomerUserId())
                .itemNumber(order.getOrderCode())
                .name(order.getCustomerUserId())
                .statusCode(order.getStatus().name())
                .date(order.getCreatedAt() != null ? order.getCreatedAt().toLocalDate().toString() : null)
                .build();
    }
}
