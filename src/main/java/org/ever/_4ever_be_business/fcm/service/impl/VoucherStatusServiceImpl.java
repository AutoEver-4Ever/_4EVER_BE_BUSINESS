package org.ever._4ever_be_business.fcm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.fcm.service.VoucherStatusService;
import org.ever._4ever_be_business.voucher.entity.PurchaseVoucher;
import org.ever._4ever_be_business.voucher.entity.SalesVoucher;
import org.ever._4ever_be_business.voucher.enums.PurchaseVoucherStatus;
import org.ever._4ever_be_business.voucher.enums.SalesVoucherStatus;
import org.ever._4ever_be_business.voucher.repository.PurchaseVoucherRepository;
import org.ever._4ever_be_business.voucher.repository.SalesVoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoucherStatusServiceImpl implements VoucherStatusService {

    private final PurchaseVoucherRepository purchaseVoucherRepository;
    private final SalesVoucherRepository salesVoucherRepository;

    @Override
    @Transactional
    public void updateVoucherStatus(String voucherId, String statusCode) {
        log.info("바우처 상태 수동 업데이트 - voucherId: {}, statusCode: {}", voucherId, statusCode);

        // PurchaseVoucher 먼저 확인
        var purchaseVoucherOpt = purchaseVoucherRepository.findById(voucherId);
        if (purchaseVoucherOpt.isPresent()) {
            PurchaseVoucher voucher = purchaseVoucherOpt.get();
            try {
                PurchaseVoucherStatus newStatus = PurchaseVoucherStatus.valueOf(statusCode);
                voucher.updateStatus(newStatus);
                purchaseVoucherRepository.save(voucher);
                log.info("PurchaseVoucher 상태 업데이트 완료 - voucherId: {}, newStatus: {}", voucherId, newStatus);
                return;
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "유효하지 않은 상태 코드입니다: " + statusCode);
            }
        }

        // SalesVoucher 확인
        var salesVoucherOpt = salesVoucherRepository.findById(voucherId);
        if (salesVoucherOpt.isPresent()) {
            SalesVoucher voucher = salesVoucherOpt.get();
            try {
                SalesVoucherStatus newStatus = SalesVoucherStatus.valueOf(statusCode);
                voucher.updateStatus(newStatus);
                salesVoucherRepository.save(voucher);
                log.info("SalesVoucher 상태 업데이트 완료 - voucherId: {}, newStatus: {}", voucherId, newStatus);
                return;
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "유효하지 않은 상태 코드입니다: " + statusCode);
            }
        }

        throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "존재하지 않는 바우처입니다: " + voucherId);
    }

    @Override
    @Transactional
    public void updateAllVoucherStatusesByDueDate() {
        log.info("전체 바우처 상태 자동 업데이트 시작");

        // PurchaseVoucher 전체 업데이트
        List<PurchaseVoucher> purchaseVouchers = purchaseVoucherRepository.findAll();
        int purchaseUpdatedCount = 0;
        for (PurchaseVoucher voucher : purchaseVouchers) {
            PurchaseVoucherStatus oldStatus = voucher.getStatus();
            voucher.updateStatusByDueDate();
            if (oldStatus != voucher.getStatus()) {
                purchaseUpdatedCount++;
            }
        }
        purchaseVoucherRepository.saveAll(purchaseVouchers);
        log.info("PurchaseVoucher 상태 업데이트 완료 - 총 {}건 중 {}건 업데이트됨",
                purchaseVouchers.size(), purchaseUpdatedCount);

        // SalesVoucher 전체 업데이트
        List<SalesVoucher> salesVouchers = salesVoucherRepository.findAll();
        int salesUpdatedCount = 0;
        for (SalesVoucher voucher : salesVouchers) {
            SalesVoucherStatus oldStatus = voucher.getStatus();
            voucher.updateStatusByDueDate();
            if (oldStatus != voucher.getStatus()) {
                salesUpdatedCount++;
            }
        }
        salesVoucherRepository.saveAll(salesVouchers);
        log.info("SalesVoucher 상태 업데이트 완료 - 총 {}건 중 {}건 업데이트됨",
                salesVouchers.size(), salesUpdatedCount);

        log.info("전체 바우처 상태 자동 업데이트 완료 - 총 업데이트: {}건",
                purchaseUpdatedCount + salesUpdatedCount);
    }
}
