package com.codehive.repository;

import com.codehive.entity.PositionApplication;
import com.codehive.entity.ProjectPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectPositionRepository extends JpaRepository<ProjectPosition,Long> {
}
