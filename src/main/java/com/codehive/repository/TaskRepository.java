package com.codehive.repository;

import com.codehive.entity.Project;
import com.codehive.entity.Task;
import com.codehive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    @Query("SELECT t FROM Task t JOIN FETCH t.assignedTo JOIN FETCH t.project WHERE t.project = :project")
    List<Task> findByProject(@Param("project") Project project);
    List<Task> findByAssignedTo(User user);
}
