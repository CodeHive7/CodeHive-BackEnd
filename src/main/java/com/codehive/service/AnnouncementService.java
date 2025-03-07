package com.codehive.service;

import com.codehive.dto.AnnouncementDto;

import java.util.List;

public interface AnnouncementService {
    AnnouncementDto createAnnouncement(Long projectId, String content, String ownerUsername);
    List<AnnouncementDto> getProjectAnnouncements(Long projectId, String username);
}
