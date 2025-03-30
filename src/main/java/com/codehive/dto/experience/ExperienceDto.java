package com.codehive.dto.experience;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExperienceDto {
    private Long id;
    private String title;
    private String company;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private boolean currentlyWorking;
    private String employmentType;
}
