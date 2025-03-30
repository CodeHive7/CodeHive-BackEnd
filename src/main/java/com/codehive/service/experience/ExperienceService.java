// ExperienceService.java
package com.codehive.service.experience;

import com.codehive.dto.experience.ExperienceDto;
import java.util.List;

public interface ExperienceService {
    ExperienceDto createExperience(String username, ExperienceDto experienceDto);
    ExperienceDto updateExperience(String username, Long id, ExperienceDto experienceDto);
    void deleteExperience(String username, Long id);
    ExperienceDto getExperience(String username, Long id);
    List<ExperienceDto> getUserExperiences(String username);
}