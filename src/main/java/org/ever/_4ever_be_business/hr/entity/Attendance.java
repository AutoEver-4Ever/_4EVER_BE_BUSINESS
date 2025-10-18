package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.hr.enums.AttendanceStatus;

import java.time.LocalDateTime;

@Entity
@Table(name="attendance")
@NoArgsConstructor
@Getter
public class Attendance extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="work_minutes")
    private Long workMinutes;

    @Column(name="work_date")
    private LocalDateTime workDate;

    @Column(name="status")
    private AttendanceStatus status;

    @Column(name="check_in")
    private LocalDateTime checkIn;

    @Column(name="check_out")
    private LocalDateTime checkOut;

    @Column(name="overtime_minutes")
    private Long overtimeMinutes;

    @ManyToOne
    @JoinColumn(name="employee_id")
    private Employee employee;

    public Attendance(Long workMinutes, LocalDateTime workDate, AttendanceStatus status, LocalDateTime checkIn, LocalDateTime checkOut, Long overtimeMinutes, Employee employee) {
        this.workMinutes = workMinutes;
        this.workDate = workDate;
        this.status = status;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.overtimeMinutes = overtimeMinutes;
        this.employee = employee;
    }
}
