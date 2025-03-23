package com.codehive.mapper;

import com.codehive.dto.UserDto;
import com.codehive.dto.education.EducationDto;
import com.codehive.dto.experience.ExperienceDto;
import com.codehive.dto.skill.SkillDto;
import com.codehive.entity.Skills.Skill;
import com.codehive.entity.User;
import com.codehive.entity.education.Education;
import com.codehive.entity.experience.Experience;
import com.codehive.repository.skill.SkillRepository;
import com.codehive.vm.UserVM;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final SkillRepository skillRepository;

    public UserDto toDto(User user) {
        if(user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setLocation(user.getLocation());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setBio(user.getBio());

        // Map educations
        dto.setEducations(user.getEducations().stream()
                .map(this::toEducationDto)
                .collect(Collectors.toList())
        );

        // Map experiences
        dto.setExperiences(user.getExperiences().stream()
                .map(this::toExperienceDto)
                .collect(Collectors.toList())
        );

        // Map Skills
        dto.setSkills(user.getSkills().stream()
                .map(skill -> {
                    SkillDto skillDto = new SkillDto();
                    skillDto.setId(skill.getId());
                    skillDto.setName(skill.getName());
                    return skillDto;
                })
                .collect(Collectors.toSet())
        );
        return dto;
    }

    public void updateUserFromVM(UserVM userVM, User user) {
        if(userVM.getFullName() != null) {
            user.setFullName(userVM.getFullName());
        }
        if(userVM.getUsername() != null) {
            user.setUsername(userVM.getUsername());
        }
        if(userVM.getEmail() != null) {
            user.setEmail(userVM.getEmail());
        }
        if(userVM.getLocation() != null) {
            user.setLocation(userVM.getLocation());
        }
        if(userVM.getPhoneNumber() != null) {
            user.setPhoneNumber(userVM.getPhoneNumber());
        }
        if(userVM.getBio() != null) {
            user.setBio(userVM.getBio());
        }

        // Update educations if provided
        if(userVM.getEducations() != null) {
            user.getEducations().clear();
            userVM.getEducations().forEach(educationDto -> {
                Education education = toEducationEntity(educationDto);
                education.setUser(user);
                user.getEducations().add(education);
            });
        }

        // Update experiences if provided
        if(userVM.getExperiences() != null) {
            user.getExperiences().clear();
            userVM.getExperiences().forEach(experienceDto -> {
                Experience experience = toExperienceEntity(experienceDto);
                experience.setUser(user);
                user.getExperiences().add(experience);
            });
        }

        // Update skills if provided
        if(userVM.getSkills() != null) {
            user.getSkills().clear();
            userVM.getSkills().forEach(skillDto -> {
                Skill skill = skillRepository.findByName(skillDto.getName())
                        .orElseGet(() -> {
                            Skill newSkill = new Skill();
                            newSkill.setName(skillDto.getName());
                            return skillRepository.save(newSkill);
                        });
                user.getSkills().add(skill);
            });
        }
    }

    private EducationDto toEducationDto(Education education) {
        EducationDto dto = new EducationDto();
        dto.setId(education.getId());
        dto.setInstitution(education.getInstitution());
        dto.setDegree(education.getDegree());
        dto.setFieldOfStudy(education.getFieldOfStudy());
        dto.setStartDate(education.getStartDate());
        dto.setEndDate(education.getEndDate());
        dto.setDescription(education.getDescription());
        dto.setCurrentlyStudying(education.isCurrentlyStudying());
        return dto;
    }

    private ExperienceDto toExperienceDto(Experience experience) {
        ExperienceDto dto = new ExperienceDto();
        dto.setId(experience.getId());
        dto.setTitle(experience.getTitle());
        dto.setCompany(experience.getCompany());
        dto.setLocation(experience.getLocation());
        dto.setStartDate(experience.getStartDate());
        dto.setEndDate(experience.getEndDate());
        dto.setDescription(experience.getDescription());
        dto.setCurrentlyWorking(experience.isCurrentlyWorking());
        dto.setEmploymentType(experience.getEmploymentType());
        return dto;
    }

    private Education toEducationEntity(EducationDto dto) {
        Education education = new Education();
        education.setId(dto.getId());
        education.setInstitution(dto.getInstitution());
        education.setDegree(dto.getDegree());
        education.setFieldOfStudy(dto.getFieldOfStudy());
        education.setStartDate(dto.getStartDate());
        education.setEndDate(dto.getEndDate());
        education.setDescription(dto.getDescription());
        education.setCurrentlyStudying(dto.isCurrentlyStudying());
        return education;
    }

    private Experience toExperienceEntity(ExperienceDto dto) {
        Experience experience = new Experience();
        experience.setId(dto.getId());
        experience.setTitle(dto.getTitle());
        experience.setCompany(dto.getCompany());
        experience.setLocation(dto.getLocation());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());
        experience.setDescription(dto.getDescription());
        experience.setCurrentlyWorking(dto.isCurrentlyWorking());
        experience.setEmploymentType(dto.getEmploymentType());
        return experience;
    }
}
