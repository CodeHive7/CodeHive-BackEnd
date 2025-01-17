package com.codehive.repository;

import com.codehive.entity.PositionApplication;
import com.codehive.entity.ProjectPosition;
import com.codehive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<PositionApplication,Long> {
    boolean existsByApplicantAndPosition(User applicant, ProjectPosition position);
    Optional<PositionApplication> findByApplicantAndPosition(User applicant, ProjectPosition position);
}
