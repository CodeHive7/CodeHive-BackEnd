package com.codehive.controller.skill;

import com.codehive.dto.skill.SkillDto;
import com.codehive.service.skill.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<SkillDto> createSkill(@RequestBody SkillDto skillDto) {
        SkillDto createdSkill = skillService.createSkill(skillDto.getName());
        return new ResponseEntity<>(createdSkill, HttpStatus.CREATED);
    }

    @PostMapping("/user")
    public ResponseEntity<SkillDto> addSkillToUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SkillDto skillDto) {
        String username = userDetails.getUsername();
        SkillDto addedSkill = skillService.addSkillToUser(username, skillDto.getName());
        return ResponseEntity.ok(addedSkill);
    }

    @DeleteMapping("/user/{skillId}")
    public ResponseEntity<Void> removeSkillFromUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long skillId) {
        String username = userDetails.getUsername();
        skillService.removeSkillFromUser(username, skillId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<Set<SkillDto>> getUserSkills(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Set<SkillDto> skills = skillService.getUserSkills(username);
        return ResponseEntity.ok(skills);
    }

    @GetMapping
    public ResponseEntity<List<SkillDto>> getAllSkills() {
        List<SkillDto> skills = skillService.getAllSkills();
        return ResponseEntity.ok(skills);
    }
}