package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.order.entity.Order;
import org.ever._4ever_be_business.order.repository.OrderRepository;
import org.ever._4ever_be_business.sd.dto.response.DashboardWorkflowItemDto;
import org.ever._4ever_be_business.sd.service.DashboardOrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardOrderServiceImpl implements DashboardOrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<DashboardWorkflowItemDto> getAllOrders(int size) {
        int limit = size > 0 ? Math.min(size, 20) : 5;

        List<DashboardWorkflowItemDto> items = orderRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit))
                .stream()
                .map(this::toDashboardItem)
                .toList();

        if (items.isEmpty()) {
            log.info("[DASHBOARD][MOCK][SD][SO] 실데이터 없음 - 내부 주문서 목업 데이터 반환");
            return buildMockInternalOrders(limit);
        }

        return items;
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

    private List<DashboardWorkflowItemDto> buildMockInternalOrders(int size) {
        int itemCount = Math.min(size > 0 ? size : 5, 5);

        return IntStream.range(0, itemCount)
                .mapToObj(i -> DashboardWorkflowItemDto.builder()
                        .itemId(UUID.randomUUID().toString())
                        .itemTitle("내부 주문 " + (i + 1))
                        .itemNumber(String.format("SO-MOCK-%04d", i + 1))
                        .name("영업 담당자 " + (i + 1))
                        .statusCode(i % 2 == 0 ? "RELEASED" : "IN_PROGRESS")
                        .date(LocalDate.now().minusDays(i).toString())
                        .build())
                .toList();
    }
}
