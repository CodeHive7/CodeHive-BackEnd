package com.codehive.repository;

import com.codehive.entity.Project;
import com.codehive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    List<Project> findByCreator(User creator);
    @Query("""
            SELECT p
            FROM Project p
            LEFT JOIN FETCH p.positions pos
            WHERE p.creator = :creator
""")
    List<Project> findByCreatorWithPositions(User creator);

    @Query("""
            SELECT p
            FROM Project p
            LEFT JOIN FETCH p.positions 
            WHERE p.id = :projectId
""")
    Optional<Project> findByIdWithPositions(@Param("projectId") Long projectId);
}
