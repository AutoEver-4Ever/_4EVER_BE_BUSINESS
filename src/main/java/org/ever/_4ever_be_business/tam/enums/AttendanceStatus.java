package org.ever._4ever_be_business.tam.enums;

public enum AttendanceStatus {
    NORMAL,         // 정상 근무 (API 응답용)
    ON_TIME,        // 정상 출근
    LATE,           // 지각
    ON_LEAVE,       // 휴가
    PRESENT,        // 출근 (정상 근무)
    ABSENT,         // 결근
    EARLY_LEAVE,    // 조퇴
    HOLIDAY,        // 휴일 (공휴일/주말 등)
    OFFICIAL_TRIP   // 출장
}
