package org.ever._4ever_be_business.voucher.repository;

import org.ever._4ever_be_business.voucher.entity.PurchaseVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseVoucherRepository extends JpaRepository<PurchaseVoucher, String> {
}
