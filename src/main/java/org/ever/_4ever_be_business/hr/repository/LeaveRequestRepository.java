package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.LeaveRequest;
import org.ever._4ever_be_business.hr.enums.LeaveRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * LeaveRequest Repository
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, String>, LeaveRequestRepositoryCustom {

    /**
     * 특정 직원의 승인된 휴가 일수 합계 조회
     *
     * @param employeeId 직원 ID
     * @return 승인된 휴가 일수 합계 (없으면 0)
     */
    @Query("SELECT COALESCE(SUM(lr.numberOfLeaveDays), 0) " +
           "FROM LeaveRequest lr " +
           "WHERE lr.employee.id = :employeeId " +
           "AND lr.status = :status")
    Integer sumApprovedLeaveDaysByEmployeeId(@Param("employeeId") String employeeId,
                                              @Param("status") LeaveRequestStatus status);
}
