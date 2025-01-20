package com.codehive.repository;

import com.codehive.entity.Project;
import com.codehive.entity.Task;
import com.codehive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByProject(Project project);
    List<Task> findByAssignedTo(User user);
}
