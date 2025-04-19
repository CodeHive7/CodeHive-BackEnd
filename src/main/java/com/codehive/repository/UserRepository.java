package com.codehive.repository;

import com.codehive.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByUsername(String username);
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    @Query("""
          SELECT DISTINCT u 
          FROM users u
          LEFT JOIN FETCH u.roles r
          LEFT JOIN FETCH r.permissions 
          WHERE u.username = :username
""")
    Optional<User> findByUsernameWithRolesAndPermissions(@Param("username") String username);
    @Query("""
          SELECT DISTINCT u 
          FROM users u
          LEFT JOIN FETCH u.roles r
          LEFT JOIN FETCH r.permissions 
          WHERE u.email = :email
""")
    Optional<User> findByEmailWithRolesAndPermissions(String email);
    Optional existsByEmail(@Param("email") String email);
}
