package org.ever._4ever_be_business.company.repository;

import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCompanyRepository extends JpaRepository<CustomerCompany, String>, CustomerCompanyRepositoryCustom {
}
