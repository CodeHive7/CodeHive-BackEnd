package com.codehive.controller.experience;

import com.codehive.dto.experience.ExperienceDto;
import com.codehive.service.experience.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    @PostMapping
    public ResponseEntity<ExperienceDto> createExperience(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ExperienceDto experienceDto) {
        String username = userDetails.getUsername();
        ExperienceDto createdExperience = experienceService.createExperience(username, experienceDto);
        return new ResponseEntity<>(createdExperience, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExperienceDto> updateExperience(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody ExperienceDto experienceDto) {
        String username = userDetails.getUsername();
        ExperienceDto updatedExperience = experienceService.updateExperience(username, id, experienceDto);
        return ResponseEntity.ok(updatedExperience);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExperience(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String username = userDetails.getUsername();
        experienceService.deleteExperience(username, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExperienceDto> getExperience(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String username = userDetails.getUsername();
        ExperienceDto experience = experienceService.getExperience(username, id);
        return ResponseEntity.ok(experience);
    }

    @GetMapping
    public ResponseEntity<List<ExperienceDto>> getUserExperiences(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<ExperienceDto> experiences = experienceService.getUserExperiences(username);
        return ResponseEntity.ok(experiences);
    }
}