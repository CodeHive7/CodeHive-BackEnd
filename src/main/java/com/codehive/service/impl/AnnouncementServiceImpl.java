package com.codehive.service.impl;

import com.codehive.dto.AnnouncementDto;
import com.codehive.entity.Announcement;
import com.codehive.entity.Project;
import com.codehive.entity.User;
import com.codehive.repository.AnnouncementRepository;
import com.codehive.repository.ApplicationRepository;
import com.codehive.repository.ProjectRepository;
import com.codehive.repository.UserRepository;
import com.codehive.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final ProjectRepository projectRepository;
    private final AnnouncementRepository announcementRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    @Override
    public AnnouncementDto createAnnouncement(Long projectId, String content, String ownerUsername) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if(!project.getCreator().getUsername().equals(ownerUsername)) {
            throw new RuntimeException("You are not authorized to create announcement for this project");
        }
        Announcement announcement = new Announcement();
        announcement.setProject(project);
        announcement.setContent(content);
        announcement.setCreatedAt(LocalDateTime.now());
        announcementRepository.save(announcement);

        AnnouncementDto dto = new AnnouncementDto();
        dto.setId(announcement.getId());
        dto.setContent(announcement.getContent());
        dto.setCreatedAt(announcement.getCreatedAt());
        return dto;
    }

    @Override
    public List<AnnouncementDto> getProjectAnnouncements(Long projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(!project.getCreator().getUsername().equals(username) && !applicationRepository.existsByApplicantAndProject(user, project)) {
            throw new RuntimeException("You are not authorized to view announcements for this project");
        }

        return announcementRepository.findByProject(project)
                .stream()
                .map(announcement -> {
                    AnnouncementDto dto = new AnnouncementDto();
                    dto.setId(announcement.getId());
                    dto.setContent(announcement.getContent());
                    dto.setCreatedAt(announcement.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
