package com.codehive.repository;

import com.codehive.Enum.ProjectStatus;
import com.codehive.entity.Category;
import com.codehive.entity.Project;
import com.codehive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    @Query("""
           SELECT p FROM Project p
           LEFT JOIN FETCH p.positions
           LEFT JOIN FETCH p.category
           WHERE p.status = :status
""")
    List<Project> findByStatusWithPositions(@Param("status") ProjectStatus status);

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
            LEFT JOIN FETCH p.category
            LEFT JOIN FETCH p.creator
            WHERE p.id = :projectId
""")
    Optional<Project> findByIdWithPositions(@Param("projectId") Long projectId);

    @Query("SELECT p FROM  Project p LEFT JOIN FETCH p.positions")
    List<Project> findAllWithPositions();

    long countByCategory(Category category);

    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = 'ACCEPTED'")
    long countActiveProjects();


}
