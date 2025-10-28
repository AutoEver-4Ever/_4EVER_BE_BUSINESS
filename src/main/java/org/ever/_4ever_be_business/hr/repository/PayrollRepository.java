package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Payroll Repository
 */
@Repository
public interface PayrollRepository extends JpaRepository<Payroll, String> {
}
