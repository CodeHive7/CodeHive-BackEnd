// EducationServiceImpl.java
package com.codehive.service.impl.education;

import com.codehive.dto.education.EducationDto;
import com.codehive.entity.User;
import com.codehive.entity.education.Education;
import com.codehive.mapper.UserMapper;
import com.codehive.repository.UserRepository;
import com.codehive.repository.education.EducationRepository;
import com.codehive.service.education.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements com.codehive.service.education.EducationService {

    private final EducationRepository educationRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public EducationDto createEducation(String username, EducationDto educationDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Education education = userMapper.toEducationEntity(educationDto);
        education.setUser(user);

        Education savedEducation = educationRepository.save(education);
        return userMapper.toEducationDto(savedEducation);
    }

    @Override
    @Transactional
    public EducationDto updateEducation(String username, Long id, EducationDto educationDto) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        if (!education.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to update this education");
        }

        // Update fields
        education.setInstitution(educationDto.getInstitution());
        education.setDegree(educationDto.getDegree());
        education.setFieldOfStudy(educationDto.getFieldOfStudy());
        education.setStartDate(educationDto.getStartDate());
        education.setEndDate(educationDto.getEndDate());
        education.setDescription(educationDto.getDescription());
        education.setCurrentlyStudying(educationDto.isCurrentlyStudying());

        Education updatedEducation = educationRepository.save(education);
        return userMapper.toEducationDto(updatedEducation);
    }

    @Override
    @Transactional
    public void deleteEducation(String username, Long id) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        if (!education.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this education");
        }

        educationRepository.delete(education);
    }

    @Override
    public EducationDto getEducation(String username, Long id) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        if (!education.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to view this education");
        }

        return userMapper.toEducationDto(education);
    }

    @Override
    public List<EducationDto> getUserEducations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Education> educations = educationRepository.findByUserId(user.getId());
        return educations.stream()
                .map(userMapper::toEducationDto)
                .collect(Collectors.toList());
    }
}