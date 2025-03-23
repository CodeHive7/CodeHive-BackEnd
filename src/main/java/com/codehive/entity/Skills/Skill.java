package com.codehive.entity.Skills;

import com.codehive.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "skills")
@Getter
@Setter
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToMany(mappedBy = "skills")
    private Set<User> users = new HashSet<>();
}
