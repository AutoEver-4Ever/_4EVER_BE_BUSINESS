package org.ever._4ever_be_business.fcm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.util.DateRangeCalculator;
import org.ever._4ever_be_business.fcm.dao.FcmStatisticsDAO;
import org.ever._4ever_be_business.fcm.dto.response.FcmPeriodStatisticsDto;
import org.ever._4ever_be_business.fcm.dto.response.FcmStatisticsDto;
import org.ever._4ever_be_business.fcm.dto.response.FcmStatisticsValueDto;
import org.ever._4ever_be_business.fcm.service.FcmStatisticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmStatisticsServiceImpl implements FcmStatisticsService {

    private final FcmStatisticsDAO fcmStatisticsDAO;

    @Override
    @Transactional(readOnly = true)
    public FcmStatisticsDto getFcmStatistics() {
        LocalDate today = LocalDate.now();
        log.info("재무관리 통계 조회 요청 - 기준일: {}", today);

        // 주간 통계
        FcmPeriodStatisticsDto weekStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.WEEK);

        // 월간 통계
        FcmPeriodStatisticsDto monthStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.MONTH);

        // 분기 통계
        FcmPeriodStatisticsDto quarterStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.QUARTER);

        // 연간 통계
        FcmPeriodStatisticsDto yearStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.YEAR);

        log.info("재무관리 통계 조회 완료");

        return new FcmStatisticsDto(weekStats, monthStats, quarterStats, yearStats);
    }

    /**
     * 기간별 통계 계산
     */
    private FcmPeriodStatisticsDto calculatePeriodStatistics(DateRangeCalculator.PeriodType periodType) {
        Map<String, LocalDate[]> dateRanges = DateRangeCalculator.getDateRanges(periodType);

        // 현재 기간과 이전 기간 추출
        LocalDate[] currentPeriod = getCurrentPeriod(dateRanges, periodType);
        LocalDate[] previousPeriod = getPreviousPeriod(dateRanges, periodType);

        // 현재 기간 데이터
        BigDecimal currentTotalSales = fcmStatisticsDAO.calculateTotalSales(
                currentPeriod[0], currentPeriod[1]
        );
        BigDecimal currentTotalPurchases = fcmStatisticsDAO.calculateTotalPurchases(
                currentPeriod[0], currentPeriod[1]
        );
        BigDecimal currentNetProfit = fcmStatisticsDAO.calculateNetProfit(
                currentPeriod[0], currentPeriod[1]
        );
        BigDecimal currentAccountsReceivable = fcmStatisticsDAO.calculateAccountsReceivable(
                currentPeriod[0], currentPeriod[1]
        );

        // 이전 기간 데이터
        BigDecimal previousTotalSales = fcmStatisticsDAO.calculateTotalSales(
                previousPeriod[0], previousPeriod[1]
        );
        BigDecimal previousTotalPurchases = fcmStatisticsDAO.calculateTotalPurchases(
                previousPeriod[0], previousPeriod[1]
        );
        BigDecimal previousNetProfit = fcmStatisticsDAO.calculateNetProfit(
                previousPeriod[0], previousPeriod[1]
        );
        BigDecimal previousAccountsReceivable = fcmStatisticsDAO.calculateAccountsReceivable(
                previousPeriod[0], previousPeriod[1]
        );

        // 증감률 계산
        Double salesDeltaRate = calculateDeltaRate(currentTotalSales, previousTotalSales);
        Double purchasesDeltaRate = calculateDeltaRate(currentTotalPurchases, previousTotalPurchases);
        Double netProfitDeltaRate = calculateDeltaRate(currentNetProfit, previousNetProfit);
        Double accountsReceivableDeltaRate = calculateDeltaRate(currentAccountsReceivable, previousAccountsReceivable);

        return new FcmPeriodStatisticsDto(
                new FcmStatisticsValueDto(currentTotalPurchases, purchasesDeltaRate),
                new FcmStatisticsValueDto(currentNetProfit, netProfitDeltaRate),
                new FcmStatisticsValueDto(currentAccountsReceivable, accountsReceivableDeltaRate),
                new FcmStatisticsValueDto(currentTotalSales, salesDeltaRate)
        );
    }

    /**
     * 현재 기간 추출
     */
    private LocalDate[] getCurrentPeriod(Map<String, LocalDate[]> dateRanges, DateRangeCalculator.PeriodType periodType) {
        return switch (periodType) {
            case WEEK -> dateRanges.get("thisWeek");
            case MONTH -> dateRanges.get("thisMonth");
            case QUARTER -> dateRanges.get("thisQuarter");
            case YEAR -> dateRanges.get("thisYear");
        };
    }

    /**
     * 이전 기간 추출
     */
    private LocalDate[] getPreviousPeriod(Map<String, LocalDate[]> dateRanges, DateRangeCalculator.PeriodType periodType) {
        return switch (periodType) {
            case WEEK -> dateRanges.get("lastWeek");
            case MONTH -> dateRanges.get("lastMonth");
            case QUARTER -> dateRanges.get("lastQuarter");
            case YEAR -> dateRanges.get("lastYear");
        };
    }

    /**
     * 증감률 계산: (현재 - 이전) / 이전
     */
    private Double calculateDeltaRate(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }

        BigDecimal delta = current.subtract(previous);
        BigDecimal rate = delta.divide(previous, 4, RoundingMode.HALF_UP);

        return rate.doubleValue();
    }
}
