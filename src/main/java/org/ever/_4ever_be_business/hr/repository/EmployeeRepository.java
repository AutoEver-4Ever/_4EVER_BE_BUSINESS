package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Employee Repository
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String>, EmployeeRepositoryCustom {
    /**
     * 특정 기간 동안 생성된 직원 수 조회
     *
     * @param start 시작 날짜
     * @param end   종료 날짜
     * @return 직원 수
     */
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 특정 날짜 이전에 생성된 직원 수 조회
     *
     * @param date 기준 날짜
     * @return 직원 수
     */
    long countByCreatedAtBefore(LocalDateTime date);
}
