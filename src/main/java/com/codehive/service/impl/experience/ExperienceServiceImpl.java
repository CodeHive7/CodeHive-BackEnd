// ExperienceServiceImpl.java
package com.codehive.service.impl.experience;

import com.codehive.dto.experience.ExperienceDto;
import com.codehive.entity.User;
import com.codehive.entity.experience.Experience;
import com.codehive.mapper.UserMapper;
import com.codehive.repository.UserRepository;
import com.codehive.repository.experience.ExperienceRepository;
import com.codehive.service.experience.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public ExperienceDto createExperience(String username, ExperienceDto experienceDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Experience experience = userMapper.toExperienceEntity(experienceDto);
        experience.setUser(user);

        Experience savedExperience = experienceRepository.save(experience);
        experienceRepository.flush();

        return userMapper.toExperienceDto(savedExperience);
    }

    @Override
    @Transactional
    public ExperienceDto updateExperience(String username, Long id, ExperienceDto experienceDto) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Experience not found"));

        if (!experience.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to update this experience");
        }

        // Update fields
        experience.setTitle(experienceDto.getTitle());
        experience.setCompany(experienceDto.getCompany());
        experience.setLocation(experienceDto.getLocation());
        experience.setStartDate(experienceDto.getStartDate());
        experience.setEndDate(experienceDto.getEndDate());
        experience.setDescription(experienceDto.getDescription());
        experience.setCurrentlyWorking(experienceDto.isCurrentlyWorking());
        experience.setEmploymentType(experienceDto.getEmploymentType());

        Experience updatedExperience = experienceRepository.save(experience);
        return userMapper.toExperienceDto(updatedExperience);
    }

    @Override
    @Transactional
    public void deleteExperience(String username, Long id) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Experience not found"));

        if (!experience.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this experience");
        }

        experienceRepository.delete(experience);
    }

    @Override
    public ExperienceDto getExperience(String username, Long id) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Experience not found"));

        if (!experience.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to view this experience");
        }

        return userMapper.toExperienceDto(experience);
    }

    @Override
    public List<ExperienceDto> getUserExperiences(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Experience> experiences = experienceRepository.findByUserId(user.getId());
        return experiences.stream()
                .map(userMapper::toExperienceDto)
                .collect(Collectors.toList());
    }
}