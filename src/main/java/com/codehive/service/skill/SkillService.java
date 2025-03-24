// SkillService.java
package com.codehive.service.skill;

import com.codehive.dto.skill.SkillDto;
import java.util.List;
import java.util.Set;

public interface SkillService {
    SkillDto createSkill(String skillName);
    SkillDto addSkillToUser(String username, String skillName);
    void removeSkillFromUser(String username, Long skillId);
    Set<SkillDto> getUserSkills(String username);
    List<SkillDto> getAllSkills();
}