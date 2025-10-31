package org.ever._4ever_be_business.order.repository;

import org.ever._4ever_be_business.order.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, String>, QuotationRepositoryCustom {
}
