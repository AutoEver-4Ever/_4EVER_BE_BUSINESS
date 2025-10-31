package org.ever._4ever_be_business.tam.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceSearchConditionVo {
    private String department;
    private String position;
    private String name;
    private LocalDate date;

    public AttendanceSearchConditionVo(String department, String position, String name, LocalDate date) {
        this.department = department;
        this.position = position;
        this.name = name;
        this.date = date;
    }
}
