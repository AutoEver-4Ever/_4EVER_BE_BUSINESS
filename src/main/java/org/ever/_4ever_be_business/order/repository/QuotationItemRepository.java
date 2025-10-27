package org.ever._4ever_be_business.order.repository;

import org.ever._4ever_be_business.order.entity.QuotationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotationItemRepository extends JpaRepository<QuotationItem, String> {
}
