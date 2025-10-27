package org.ever._4ever_be_business.hr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.dao.PayrollDAO;
import org.ever._4ever_be_business.hr.dto.response.PayrollListItemDto;
import org.ever._4ever_be_business.hr.dto.response.PaystubDetailDto;
import org.ever._4ever_be_business.hr.service.PayrollService;
import org.ever._4ever_be_business.hr.vo.PayrollSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayrollDAO payrollDAO;

    @Override
    @Transactional(readOnly = true)
    public PaystubDetailDto getPaystubDetail(String paystubId) {
        log.info("급여 명세서 상세 조회 요청 - paystubId: {}", paystubId);

        PaystubDetailDto result = payrollDAO.findPaystubDetailById(paystubId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "급여 명세서를 찾을 수 없습니다."));

        log.info("급여 명세서 상세 조회 성공 - paystubId: {}, employeeName: {}",
                paystubId, result.getEmployee().getEmployeeName());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PayrollListItemDto> getPayrollList(PayrollSearchConditionVo condition, Pageable pageable) {
        log.info("급여 명세서 목록 조회 요청 - year: {}, month: {}, name: {}, department: {}, position: {}, page: {}, size: {}",
                condition.getYear(), condition.getMonth(), condition.getName(),
                condition.getDepartment(), condition.getPosition(),
                pageable.getPageNumber(), pageable.getPageSize());

        Page<PayrollListItemDto> result = payrollDAO.findPayrollList(condition, pageable);

        log.info("급여 명세서 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return result;
    }
}
