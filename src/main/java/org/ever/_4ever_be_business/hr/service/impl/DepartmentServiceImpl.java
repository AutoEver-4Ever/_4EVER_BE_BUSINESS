package org.ever._4ever_be_business.hr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.dao.DepartmentDAO;
import org.ever._4ever_be_business.hr.dto.response.DepartmentDetailDto;
import org.ever._4ever_be_business.hr.dto.response.DepartmentListItemDto;
import org.ever._4ever_be_business.hr.dto.response.DepartmentMemberDto;
import org.ever._4ever_be_business.hr.service.DepartmentService;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDAO departmentDAO;

    @Override
    @Transactional(readOnly = true)
    public DepartmentDetailDto getDepartmentDetail(String departmentId) {
        log.info("부서 상세 정보 조회 요청 - departmentId: {}", departmentId);

        DepartmentDetailDto result = departmentDAO.findDepartmentDetailById(departmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "부서 정보를 찾을 수 없습니다."));

        log.info("부서 상세 정보 조회 성공 - departmentId: {}, departmentName: {}, headcount: {}",
                departmentId, result.getDepartmentName(), result.getHeadcount());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentListItemDto> getDepartmentList(String status, Pageable pageable) {
        log.info("부서 목록 조회 요청 - status: {}, page: {}, size: {}", status, pageable.getPageNumber(), pageable.getPageSize());

        Page<DepartmentListItemDto> result = departmentDAO.findDepartmentList(status, pageable);

        log.info("부서 목록 조회 성공 - total: {}, size: {}", result.getTotalElements(), result.getContent().size());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentMemberDto> getDepartmentMembers(String departmentId) {
        log.info("부서 구성원 목록 조회 요청 - departmentId: {}", departmentId);

        List<DepartmentMemberDto> result = departmentDAO.findDepartmentMembers(departmentId);

        log.info("부서 구성원 목록 조회 성공 - count: {}", result.size());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getInternalUserIdsByDepartmentName(String departmentName) {
        log.info("부서명으로 InternelUser userId 목록 조회 요청 - departmentName: {}", departmentName);
        List<String> userIds = departmentDAO.findInternalUserIdsByDepartmentName(departmentName);
        log.info("부서명으로 InternelUser userId 목록 조회 성공 - count: {}", userIds.size());
        return userIds;
    }
}
