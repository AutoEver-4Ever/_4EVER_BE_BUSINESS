package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.hr.enums.Gender;

import java.time.LocalDateTime;

@Entity
@Table(name="internel_user")
@NoArgsConstructor
@Getter
public class InternelUser extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private Long userId;

    @Column(name="name")
    private String name;

    @Column(name="employee_code")
    private String employeeCode;

    @ManyToOne
    @JoinColumn(name="position_id")
    private Position position;

    @Column(name="employee_name")
    private Gender gender;

    @Column(name="birth_date")
    private LocalDateTime birthDate;

    public InternelUser(Long userId, String name, String employeeCode, Position position, Gender gender, LocalDateTime birthDate) {
        this.userId = userId;
        this.name = name;
        this.employeeCode = employeeCode;
        this.position = position;
        this.gender = gender;
        this.birthDate = birthDate;
    }
}
