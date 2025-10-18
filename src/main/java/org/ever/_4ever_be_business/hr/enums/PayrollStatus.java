package org.ever._4ever_be_business.hr.enums;

public enum PayrollStatus {
    DRAFT,              // 초안(집계 전)
    PENDING_APPROVAL,   // 승인 대기
    APPROVED,           // 승인 완료

    CALCULATING,        // 급여 계산 중
    CALCULATED,         // 계산 완료(전표/지급 전 검증 단계)

    PENDING_PAYMENT,    // 지급 지시 대기(이체 파일/펌뱅킹 전)
    PAID,               // 지급 완료(은행 이체/현금 지급 완료)
    PARTIALLY_PAID,     // 일부 지급

    POSTING,            // 회계 반영 중
    POSTED,             // 회계 반영 완료(전표 확정)

    ON_HOLD,            // 보류(이의제기/감사)
    ADJUSTING,          // 정정 처리 중(추가공제/보너스/소급)
    ADJUSTED,           // 정정 반영 완료

    FAILED,             // 계산/지급/전표 어느 단계에서든 실패
    CANCELLED,          // 승인 전 취소
    REVERSED,           // 지급/전표 이후 취소(역분개/반제)
    CLOSED              // 마감(추가 변경 불가)
}
