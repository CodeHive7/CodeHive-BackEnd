package com.codehive.entity;

import com.codehive.Enum.ProjectStage;
import com.codehive.Enum.ProjectStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Getter
@Setter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private ProjectStage stage;

    private String websiteUrl;
    private String problemToFix;

    private String question1;
    private String question2;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectPosition> positions = new HashSet<>();

}
