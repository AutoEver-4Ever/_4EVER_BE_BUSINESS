package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.sd.dto.response.SupplierOrderWorkflowItemDto;
import org.ever._4ever_be_business.sd.service.SdSupplierOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SdSupplierOrderServiceImpl implements SdSupplierOrderService {

    @Override
    public Page<SupplierOrderWorkflowItemDto> getSupplierOrderList(String supplierUserId, Pageable pageable) {
        // 1단계: Repository 연동 전까지 빈 페이지 반환
        log.info("[BUSINESS][SD] 대시보드 공급사 주문서 목록 조회 - userId: {}, page: {}, size: {}",
                supplierUserId, pageable.getPageNumber(), pageable.getPageSize());
        return new PageImpl<>(List.of(), pageable, 0);
    }
}
