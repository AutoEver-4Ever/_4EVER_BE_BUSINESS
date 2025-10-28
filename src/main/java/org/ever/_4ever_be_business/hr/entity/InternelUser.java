package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.hr.enums.Gender;

import java.time.LocalDateTime;

@Entity
@Table(name="internel_user")
@NoArgsConstructor
@Getter
public class InternelUser extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

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

    @Column(name="hire_date")
    private LocalDateTime hireDate;

    @Column(name="address")
    private String address;

    @Column(name="email")
    private String email;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="department_start_at")
    private LocalDateTime departmentStartAt;

    @Column(name="education")
    private String education;

    @Column(name="career", length = 100)
    private String career;


    public InternelUser(Long userId, String name, String employeeCode, Position position, Gender gender, LocalDateTime birthDate, LocalDateTime hireDate, String address, LocalDateTime departmentStartAt, String education, String career) {
        this.userId = userId;
        this.name = name;
        this.employeeCode = employeeCode;
        this.position = position;
        this.gender = gender;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.address = address;
        this.departmentStartAt = departmentStartAt;
        this.education = education;
        this.career = career;
    }

    /**
     * 목업 데이터 생성용 생성자 (ID 포함)
     */
    public InternelUser(String id, Long userId, String name, String employeeCode, Position position,
                        Gender gender, LocalDateTime birthDate, LocalDateTime hireDate, String address,
                        String email, String phoneNumber, LocalDateTime departmentStartAt,
                        String education, String career) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.employeeCode = employeeCode;
        this.position = position;
        this.gender = gender;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.departmentStartAt = departmentStartAt;
        this.education = education;
        this.career = career;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }

    /**
     * 직원 정보 수정
     *
     * @param name     이름
     * @param position 직급
     */
    public void updateEmployeeInfo(String name, Position position) {
        if (name != null) {
            this.name = name;
        }
        if (position != null) {
            this.position = position;
        }
    }
}
