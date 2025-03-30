// EducationService.java
package com.codehive.service.education;

import com.codehive.dto.education.EducationDto;
import java.util.List;

public interface EducationService {
    EducationDto createEducation(String username, EducationDto educationDto);
    EducationDto updateEducation(String username, Long id, EducationDto educationDto);
    void deleteEducation(String username, Long id);
    EducationDto getEducation(String username, Long id);
    List<EducationDto> getUserEducations(String username);
}