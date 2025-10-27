package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * LeaveRequest Repository
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, String>, LeaveRequestRepositoryCustom {
}
