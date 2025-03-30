// SkillServiceImpl.java
package com.codehive.service.impl.skill;

import com.codehive.dto.skill.SkillDto;
import com.codehive.entity.Skills.Skill;
import com.codehive.entity.User;
import com.codehive.mapper.UserMapper;
import com.codehive.repository.UserRepository;
import com.codehive.repository.skill.SkillRepository;
import com.codehive.service.skill.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public SkillDto createSkill(String skillName) {
        if (skillRepository.findByName(skillName).isPresent()) {
            throw new RuntimeException("Skill already exists");
        }

        Skill skill = new Skill();
        skill.setName(skillName);

        Skill savedSkill = skillRepository.save(skill);

        SkillDto skillDto = new SkillDto();
        skillDto.setId(savedSkill.getId());
        skillDto.setName(savedSkill.getName());

        return skillDto;
    }

    @Override
    @Transactional
    public SkillDto addSkillToUser(String username, String skillName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Skill skill = skillRepository.findByName(skillName)
                .orElseGet(() -> {
                    Skill newSkill = new Skill();
                    newSkill.setName(skillName);
                    return skillRepository.save(newSkill);
                });

        user.getSkills().add(skill);
        skill.getUsers().add(user);

        userRepository.saveAndFlush(user);

        SkillDto skillDto = new SkillDto();
        skillDto.setId(skill.getId());
        skillDto.setName(skill.getName());
        return skillDto;
    }

    @Override
    @Transactional
    public void removeSkillFromUser(String username, Long skillId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        user.getSkills().remove(skill);
        userRepository.save(user);
    }

    @Override
    public Set<SkillDto> getUserSkills(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getSkills().stream()
                .map(skill -> {
                    SkillDto skillDto = new SkillDto();
                    skillDto.setId(skill.getId());
                    skillDto.setName(skill.getName());
                    return skillDto;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public List<SkillDto> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(skill -> {
                    SkillDto skillDto = new SkillDto();
                    skillDto.setId(skill.getId());
                    skillDto.setName(skill.getName());
                    return skillDto;
                })
                .collect(Collectors.toList());
    }
}