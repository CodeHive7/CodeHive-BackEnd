package com.codehive.entity;

import com.codehive.Enum.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "position_applications")
@Getter
@Setter
public class PositionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    private ProjectPosition position;

    @Column(length = 1000)
    private String answer1;

    @Column(length = 1000)
    private String answer2;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;

}
