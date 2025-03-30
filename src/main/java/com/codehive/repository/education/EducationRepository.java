package com.codehive.repository.education;

import com.codehive.entity.education.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education,Long> {
    List<Education> findByUserId(Long userId);
}
