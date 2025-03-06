package com.codehive.repository;

import com.codehive.Enum.ApplicationStatus;
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
    @Query("SELECT COUNT(pa) > 0 FROM PositionApplication pa " +
           "JOIN pa.position pos " +
           "WHERE pa.applicant = :applicant AND pos.project = :project")
    boolean existsByApplicantAndProject(@Param("applicant") User applicant, @Param("project") Project project);

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

    boolean existsByApplicantAndPosition_ProjectAndStatus(User applicant, Project project , ApplicationStatus status);

    @Query("""
           SELECT pa FROM PositionApplication pa
           JOIN pa.position pos
           WHERE pos.project = :project AND pa.status = :status
""")
    List<PositionApplication> findByProjectAndStatus( @Param("project") Project project, @Param("status") ApplicationStatus status);

    @Query("SELECT COUNT(DISTINCT pa.applicant) FROM PositionApplication pa")
    long countApplicants();

    @Query("SELECT COUNT(pa) FROM PositionApplication pa WHERE pa.status = 'PENDING'")
    long countPendingApplications();



}
