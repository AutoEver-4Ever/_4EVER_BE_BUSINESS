package org.ever._4ever_be_business.hr.dao;

import org.ever._4ever_be_business.hr.dto.response.DepartmentDetailDto;
import org.ever._4ever_be_business.hr.dto.response.DepartmentListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DepartmentDAO {
    /**
     * 부서 상세 정보 조회
     *
     * @param departmentId 부서 ID
     * @return 부서 상세 정보
     */
    Optional<DepartmentDetailDto> findDepartmentDetailById(String departmentId);

    /**
     * 부서 목록 조회 (페이징)
     *
     * @param status 부서 상태 (ACTIVE/INACTIVE)
     * @param pageable 페이징 정보
     * @return 부서 목록
     */
    Page<DepartmentListItemDto> findDepartmentList(String status, Pageable pageable);
}
