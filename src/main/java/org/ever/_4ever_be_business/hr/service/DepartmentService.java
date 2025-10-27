package org.ever._4ever_be_business.hr.service;

import org.ever._4ever_be_business.hr.dto.response.DepartmentDetailDto;
import org.ever._4ever_be_business.hr.dto.response.DepartmentListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {
    /**
     * 부서 상세 정보 조회
     *
     * @param departmentId 부서 ID
     * @return 부서 상세 정보
     */
    DepartmentDetailDto getDepartmentDetail(String departmentId);

    /**
     * 부서 목록 조회
     *
     * @param status 부서 상태
     * @param pageable 페이징 정보
     * @return 부서 목록
     */
    Page<DepartmentListItemDto> getDepartmentList(String status, Pageable pageable);
}
