package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.hr.enums.TrainingCategory;

@Entity
@Table(name="training")
@NoArgsConstructor
@Getter
public class Training extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="training_name")
    private String trainingName;

    @Column(name="training_category")
    private TrainingCategory category;

    @Column(name="duration_hours")
    private Long durationHours;

    @Column(name="delivery_method")
    private String deliveryMethod;

    @Column(name="enrolled")
    private Long enrolled;

    @Column(name="description", length = 50)
    private String description;

    @Column(name="status")
    private Boolean status;

    public Training(String trainingName, TrainingCategory category, Long durationHours, String deliveryMethod, Long enrolled, String description, Boolean status) {
        this.trainingName = trainingName;
        this.category = category;
        this.durationHours = durationHours;
        this.deliveryMethod = deliveryMethod;
        this.enrolled = enrolled;
        this.description = description;
        this.status = status;
    }
}
