package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;

import java.time.LocalDateTime;

@Entity
@Table(name="department")
@NoArgsConstructor
@Getter
public class Department extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="department_code")
    private String departmentCode;

    @Column(name="department_name")
    private String departmentName;

    @Column(name="description", length = 50)
    private String description;

    @Column(name="establishment_date")
    private LocalDateTime establishmentDate;

    public Department(String departmentCode, String departmentName, String description, LocalDateTime establishmentDate) {
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.description = description;
        this.establishmentDate = establishmentDate;
    }
}
