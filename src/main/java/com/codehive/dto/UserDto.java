package com.codehive.dto;

import com.codehive.dto.education.EducationDto;
import com.codehive.dto.experience.ExperienceDto;
import com.codehive.dto.skill.SkillDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserDto {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String location;
    private String phoneNumber;
    private Set<String> roles ;
    private String status;
    private String bio;
    private List<EducationDto> educations = new ArrayList<>();
    private List<ExperienceDto> experiences = new ArrayList<>();
    private Set<SkillDto> skills = new HashSet<>();
}
