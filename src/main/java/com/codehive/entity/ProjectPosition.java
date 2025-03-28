package com.codehive.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "project_positions")
public class ProjectPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roleName;
    private boolean paid;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
