package com.codehive.controller;

import com.codehive.dto.AnnouncementDto;
import com.codehive.dto.CreateAnnouncementRequest;
import com.codehive.service.AnnouncementService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;

    @PreAuthorize("hasAuthority('CREATE_ANNOUNCEMENT')")
    @PostMapping("/{projectId}")
    public ResponseEntity<AnnouncementDto> createAnnouncement(@PathVariable Long projectId, @RequestBody CreateAnnouncementRequest request, @AuthenticationPrincipal User principal) {
        String  username = principal.getUsername();
        AnnouncementDto dto = announcementService.createAnnouncement(projectId, request.getContent(), username);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAuthority('READ_ANNOUNCEMENT')")
    @GetMapping("/{projectId}")
    public ResponseEntity<List<AnnouncementDto>> getAnnouncements(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User principal) {
        String username = principal.getUsername();
        List<AnnouncementDto> announcements = announcementService.getProjectAnnouncements(projectId, username);
        return ResponseEntity.ok(announcements);
    }
}
