package org.ever._4ever_be_business.company.dao;

import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.sd.dto.request.CreateCustomerRequestDto;
import org.ever._4ever_be_business.sd.dto.request.UpdateCustomerRequestDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerDetailDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerListItemDto;
import org.ever._4ever_be_business.sd.vo.CustomerSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomerCompanyDAO {
    /**
     * 고객사 상세 정보 조회
     *
     * @param customerId 고객사 ID
     * @return 고객사 상세 정보
     */
    Optional<CustomerDetailDto> findCustomerDetailById(String customerId);

    /**
     * 고객사 목록 조회 (검색 + 페이징)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return 고객사 목록
     */
    Page<CustomerListItemDto> findCustomerList(CustomerSearchConditionVo condition, Pageable pageable);

    /**
     * 고객사 등록
     *
     * @param dto 고객사 등록 요청 정보
     * @return 등록된 고객사 정보
     */
    CustomerCompany saveCustomer(CreateCustomerRequestDto dto);

    /**
     * 고객사 정보 수정
     *
     * @param customerId 고객사 ID
     * @param dto 수정할 고객사 정보
     */
    void updateCustomer(String customerId, UpdateCustomerRequestDto dto);

    /**
     * 고객사 삭제 (Soft Delete)
     *
     * @param customerId 고객사 ID
     */
    void deleteCustomer(String customerId);
}
