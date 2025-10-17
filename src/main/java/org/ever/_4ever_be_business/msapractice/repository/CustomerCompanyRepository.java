package org.ever._4ever_be_business.msapractice.repository;

import org.ever._4ever_be_business.msapractice.entity.CustomerCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerCompanyRepository extends JpaRepository<CustomerCompany, Long> {
    Optional<CustomerCompany> findByBusinessNumber(String businessNumber);
    Optional<CustomerCompany> findByCustomerUserId(String customerUserId);
}