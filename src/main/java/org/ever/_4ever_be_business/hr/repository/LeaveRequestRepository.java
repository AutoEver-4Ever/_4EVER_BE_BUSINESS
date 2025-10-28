package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.LeaveRequest;
import org.ever._4ever_be_business.hr.enums.LeaveRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

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

    /**
     * 특정 직원의 1년 이내 승인된 휴가 일수 합계 조회
     *
     * @param employeeId 직원 ID
     * @param startDate 시작 날짜 (1년 전)
     * @param status 휴가 신청 상태
     * @return 승인된 휴가 일수 합계 (없으면 0)
     */
    @Query("SELECT COALESCE(SUM(lr.numberOfLeaveDays), 0) " +
           "FROM LeaveRequest lr " +
           "WHERE lr.employee.id = :employeeId " +
           "AND lr.status = :status " +
           "AND lr.startDate >= :startDate")
    Integer sumApprovedLeaveDaysInLastYear(@Param("employeeId") String employeeId,
                                            @Param("status") LeaveRequestStatus status,
                                            @Param("startDate") LocalDateTime startDate);
}
