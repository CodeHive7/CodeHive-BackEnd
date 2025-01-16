package com.codehive.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column( name = "email" , nullable = false, unique = true)
    private String email;
    @Column( name = "password",nullable = false)
    private String password;
    @Column( name = "location")
    private String location;
    @Column( name = "phone_number")
    private String phoneNumber;
    @Column( name = "is_active" , nullable = false)
    private boolean isActive = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Project> projects = new HashSet<>();
}
