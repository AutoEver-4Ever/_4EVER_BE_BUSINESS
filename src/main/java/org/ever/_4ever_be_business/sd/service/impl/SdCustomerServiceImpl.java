package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.company.dao.CustomerCompanyDAO;
import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.sd.dto.request.CreateCustomerRequestDto;
import org.ever._4ever_be_business.sd.dto.request.UpdateCustomerRequestDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerDetailDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerListItemDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerListResponseDto;
import org.ever._4ever_be_business.sd.dto.response.PageInfo;
import org.ever._4ever_be_business.sd.service.SdCustomerService;
import org.ever._4ever_be_business.sd.vo.CustomerDetailVo;
import org.ever._4ever_be_business.sd.vo.CustomerSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SdCustomerServiceImpl implements SdCustomerService {

    private final CustomerCompanyDAO customerCompanyDAO;

    @Override
    @Transactional(readOnly = true)
    public CustomerDetailDto getCustomerDetail(CustomerDetailVo vo) {
        log.info("고객사 상세 정보 조회 요청 - customerId: {}", vo.getCustomerId());

        CustomerDetailDto result = customerCompanyDAO.findCustomerDetailById(vo.getCustomerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

        log.info("고객사 상세 정보 조회 성공 - customerName: {}", result.getCustomerName());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerListResponseDto getCustomerList(CustomerSearchConditionVo condition, Pageable pageable) {
        log.info("고객사 목록 조회 요청 - status: {}, type: {}, search: {}",
                condition.getStatus(), condition.getType(), condition.getSearch());

        Page<CustomerListItemDto> page = customerCompanyDAO.findCustomerList(condition, pageable);

        // Page 객체를 CustomerListResponseDto로 변환
        PageInfo pageInfo = new PageInfo(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );

        CustomerListResponseDto result = new CustomerListResponseDto(
                page.getContent(),
                pageInfo
        );

        log.info("고객사 목록 조회 성공 - totalElements: {}, totalPages: {}",
                page.getTotalElements(), page.getTotalPages());

        return result;
    }

    @Override
    @Transactional
    public String createCustomer(CreateCustomerRequestDto dto) {
        log.info("고객사 등록 요청 - companyName: {}, businessNumber: {}",
                dto.getCompanyName(), dto.getBusinessNumber());

        // DAO를 통해 고객사 저장
        CustomerCompany savedCustomer = customerCompanyDAO.saveCustomer(dto);

        log.info("고객사 등록 성공 - customerId: {}, customerCode: {}",
                savedCustomer.getId(), savedCustomer.getCompanyCode());

        return savedCustomer.getId();
    }

    @Override
    @Transactional
    public void updateCustomer(String customerId, UpdateCustomerRequestDto dto) {
        log.info("고객사 정보 수정 요청 - customerId: {}, customerName: {}",
                customerId, dto.getCustomerName());

        customerCompanyDAO.updateCustomer(customerId, dto);

        log.info("고객사 정보 수정 성공 - customerId: {}", customerId);
    }

    @Override
    @Transactional
    public void deleteCustomer(String customerId) {
        log.info("고객사 삭제 요청 - customerId: {}", customerId);

        customerCompanyDAO.deleteCustomer(customerId);

        log.info("고객사 삭제 성공 (Soft Delete) - customerId: {}", customerId);
    }
}
