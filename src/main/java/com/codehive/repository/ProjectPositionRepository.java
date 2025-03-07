package com.codehive.repository;

import com.codehive.entity.PositionApplication;
import com.codehive.entity.Project;
import com.codehive.entity.ProjectPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectPositionRepository extends JpaRepository<ProjectPosition,Long> {

}
