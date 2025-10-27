package org.ever._4ever_be_business.fcm.service;

import org.ever._4ever_be_business.fcm.dto.response.FcmStatisticsDto;

public interface FcmStatisticsService {
    /**
     * 재무관리 통계 조회 (주/월/분기/년)
     *
     * @return 재무관리 통계
     */
    FcmStatisticsDto getFcmStatistics();
}
