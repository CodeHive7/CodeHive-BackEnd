package com.codehive.vm;

import com.codehive.dto.education.EducationDto;
import com.codehive.dto.experience.ExperienceDto;
import com.codehive.dto.skill.SkillDto;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserVM {

    private String fullName;
    private String username;
    private String email;
    private String location;
    private String phoneNumber;
    private String bio;
    private List<EducationDto> educations = new ArrayList<>();
    private List<ExperienceDto> experiences = new ArrayList<>();
    private Set<SkillDto> skills = new HashSet<>();
}
