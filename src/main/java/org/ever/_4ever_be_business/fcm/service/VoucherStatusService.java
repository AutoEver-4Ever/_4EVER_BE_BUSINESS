package org.ever._4ever_be_business.fcm.service;

public interface VoucherStatusService {
    /**
     * 바우처 상태를 수동으로 업데이트합니다.
     *
     * @param voucherId 바우처 ID
     * @param statusCode 새로운 상태 코드
     */
    void updateVoucherStatus(String voucherId, String statusCode);

    /**
     * 모든 바우처의 상태를 지급 기한 기준으로 자동 업데이트합니다.
     * 스케줄러에서 호출됩니다.
     */
    void updateAllVoucherStatusesByDueDate();
}
