package com.codehive.repository;

import com.codehive.entity.PositionApplication;
import com.codehive.entity.Project;
import com.codehive.entity.ProjectPosition;
import com.codehive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<PositionApplication,Long> {
    boolean existsByApplicantAndPosition(User applicant, ProjectPosition position);
    Optional<PositionApplication> findByApplicantAndPosition(User applicant, ProjectPosition position);
    List<PositionApplication> findByApplicant(User applicant);

    @Query("""
            SELECT pa FROM PositionApplication pa
            JOIN FETCH pa.position pos
            JOIN FETCH pos.project proj
            LEFT JOIN FETCH proj.positions
            WHERE pa.applicant = :applicant
""")
    List<PositionApplication> findByApplicantWithProject(@Param("applicant") User applicant);

    @Query("SELECT pa FROM PositionApplication pa " +
           "JOIN FETCH pa.position pas " +
           "WHERE pas.project = :project")
    List<PositionApplication> findByProject(@Param("project") Project project);
}
