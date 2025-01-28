package com.codehive.service.impl;

import com.codehive.Enum.ApplicationStatus;
import com.codehive.dto.ApplicantResponseDto;
import com.codehive.dto.ApplicationUpdateRequest;
import com.codehive.dto.CreateProjectRequest;
import com.codehive.dto.ProjectResponseDto;
import com.codehive.entity.*;
import com.codehive.mapper.ApplicantMapper;
import com.codehive.mapper.ProjectMapper;
import com.codehive.mapper.ProjectPositionMapper;
import com.codehive.repository.ApplicationRepository;
import com.codehive.repository.CategoryRepository;
import com.codehive.repository.ProjectRepository;
import com.codehive.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectPositionMapper positionMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicantMapper applicantMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private final User creator = new User();
    private final Project project = new Project();

    @Test
    void createProject_ValidRequest_ShouldCreateProject() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Test Project");
        request.setStage("IN_DEVELOPMENT");
        request.setCustomCategory("New Category");

        User user = new User();
        user.setUsername("test_user");

        Category category = new Category();
        category.setName("New Category");

        Project project = new Project();
        project.setName("Test Project");

        ProjectResponseDto expectedResponse = new ProjectResponseDto();
        expectedResponse.setName("Test Project");

        when(userRepository.findByUsername("test_user")).thenReturn(Optional.of(user));
        when(categoryRepository.findByName("New Category")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(projectMapper.toEntity(request)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(expectedResponse);

        ProjectResponseDto result = projectService.createProject(request, "test_user");

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectRepository, times(1)).save(project);

    }

    @Test
    void createProject_UserNotFound_ShouldThrowException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.createProject(new CreateProjectRequest(), "nonexistent")
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void createProject_MissingStage_ShouldThrowException() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Test Project");
        request.setCustomCategory("New Category");

        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.createProject(request, "testuser")
        );

        assertEquals("Project stage must be selected", exception.getMessage());
    }

    @Test
    void createProject_InvalidStage_ShouldThrowException() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Test Project");
        request.setStage("INVALID_STAGE");
        request.setCustomCategory("New Category");

        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.createProject(request, "testuser")
        );

        assertTrue(exception.getMessage().contains("Invalid project stage"));
    }

    @Test
    void createProject_MissingCategory_ShouldThrowException() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Test Project");
        request.setStage("IN_DEVELOPMENT");

        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.createProject(request, "testuser")
        );

        assertEquals("A category must be selected or provided", exception.getMessage());
    }

    // Scenario 6: Valid project with selected category
    @Test
    void createProject_ValidWithSelectedCategory_ShouldCreateProject() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Test Project");
        request.setStage("IN_DEVELOPMENT");
        request.setSelectedCategory("Existing Category");

        User user = new User();
        user.setUsername("testuser");

        Category category = new Category();
        category.setName("Existing Category");

        Project project = new Project();
        project.setName("Test Project");

        ProjectResponseDto expectedResponse = new ProjectResponseDto();
        expectedResponse.setName("Test Project");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(categoryRepository.findByName("Existing Category")).thenReturn(Optional.of(category));
        when(projectMapper.toEntity(request)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(expectedResponse);

        ProjectResponseDto result = projectService.createProject(request, "testuser");

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void createProject_DuplicateCustomCategory_ShouldReuseCategory() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Test Project");
        request.setStage("IN_DEVELOPMENT");
        request.setCustomCategory("Duplicate Category");

        User user = new User();
        user.setUsername("testuser");

        Category category = new Category();
        category.setName("Duplicate Category");

        Project project = new Project();
        project.setName("Test Project");

        ProjectResponseDto expectedResponse = new ProjectResponseDto();
        expectedResponse.setName("Test Project");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(categoryRepository.findByName("Duplicate Category")).thenReturn(Optional.of(category));
        when(projectMapper.toEntity(request)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(expectedResponse);

        ProjectResponseDto result = projectService.createProject(request, "testuser");

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getProjectsUserAppliedTo_ValidUser_ShouldReturnApplications() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        PositionApplication application = new PositionApplication();
        application.setFeedback("Well done");
        application.setStatus(ApplicationStatus.ACCEPTED);

        ProjectPosition position = new ProjectPosition();
        position.setProject(project);
        application.setPosition(position);

        List<PositionApplication> applications = List.of(application);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(applicationRepository.findByApplicantWithProject(user)).thenReturn(applications);

        ProjectResponseDto expectedDto = new ProjectResponseDto();
        expectedDto.setName("Test Project");
        expectedDto.setFeedback("Well done");
        expectedDto.setApplicationStatus("ACCEPTED");

        when(projectMapper.toDto(any(Project.class))).thenReturn(expectedDto);

        List<ProjectResponseDto> result = projectService.getProjectsUserAppliedTo("testuser");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Well done", result.get(0).getFeedback());
        assertEquals("ACCEPTED", result.get(0).getApplicationStatus());

        verify(userRepository).findByUsername("testuser");
        verify(applicationRepository).findByApplicantWithProject(user);
    }

    @Test
    void getApplicantsForProject_ValidProject_ShouldReturnApplicants() {
        creator.setUsername("testuser");
        project.setCreator(creator);

        PositionApplication application = new PositionApplication();
        List<PositionApplication> applications = List.of(application);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(applicationRepository.findByProject(project)).thenReturn(applications);

        ApplicantResponseDto applicantResponseDto = new ApplicantResponseDto();
        when(applicantMapper.toDto(any(PositionApplication.class))).thenReturn(applicantResponseDto);

        List<ApplicantResponseDto> result = projectService.getApplicantsForProject(1L, "testuser");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(applicationRepository).findByProject(project);
    }

    @Test
    void getApplicantsForProject_NotOwner_ShouldThrowException() {
        creator.setUsername("anotheruser");
        project.setCreator(creator);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.getApplicantsForProject(1L, "testuser")
        );

        assertEquals("You are not authorized to view applicantss for this project", exception.getMessage());
        verify(projectRepository).findById(1L);
        verifyNoInteractions(applicationRepository);
    }

    @Test
    void updateApplicationStatus_ValidRequest_ShouldUpdateStatus() {
        creator.setUsername("testuser");
        project.setCreator(creator);

        PositionApplication application1 = new PositionApplication();
        application1.setId(1L);
        application1.setPosition(new ProjectPosition());
        application1.getPosition().setProject(project);

        PositionApplication application2 = new PositionApplication();
        application2.setId(2L);
        application2.setPosition(new ProjectPosition());
        application2.getPosition().setProject(project);

        List<PositionApplication> applications = List.of(application1, application2);

        ApplicationUpdateRequest request = new ApplicationUpdateRequest();
        request.setApplicationIds(List.of(1L, 2L));
        request.setAccept(true);

        when(applicationRepository.findAllById(request.getApplicationIds())).thenReturn(applications);

        projectService.updateApplicationStatus("testuser", request);

        assertEquals(ApplicationStatus.ACCEPTED, application1.getStatus());
        assertEquals(ApplicationStatus.ACCEPTED, application2.getStatus());
        verify(applicationRepository).saveAll(applications);
    }

    @Test
    void updateApplicationStatus_NotAuthorized_ShouldThrowException() {
        creator.setUsername("anotheruser");
        project.setCreator(creator);

        PositionApplication application = new PositionApplication();
        application.setId(1L);
        application.setPosition(new ProjectPosition());
        application.getPosition().setProject(project);

        List<PositionApplication> applications = List.of(application);

        ApplicationUpdateRequest request = new ApplicationUpdateRequest();
        request.setApplicationIds(List.of(1L));
        request.setAccept(false);

        when(applicationRepository.findAllById(request.getApplicationIds())).thenReturn(applications);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.updateApplicationStatus("testuser", request)
        );

        assertEquals("You are not authorized to update this application", exception.getMessage());
        verify(applicationRepository).findAllById(request.getApplicationIds());
    }
}