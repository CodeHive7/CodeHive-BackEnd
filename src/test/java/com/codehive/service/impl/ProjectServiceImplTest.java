package com.codehive.service.impl;

import com.codehive.Enum.ApplicationStatus;
import com.codehive.Enum.ProjectStage;
import com.codehive.Enum.ProjectStatus;
import com.codehive.dto.*;
import com.codehive.entity.*;
import com.codehive.exception.PositionUnavailableException;
import com.codehive.mapper.ApplicantMapper;
import com.codehive.mapper.ProjectMapper;
import com.codehive.mapper.ProjectPositionMapper;
import com.codehive.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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
    private ProjectPositionRepository projectPositionRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ApplicantMapper applicantMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User user;
    private Project project;
    private Category category;
    private ProjectPosition position;
    private CreateProjectRequest createRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setCreator(user);
        project.setCategory(category);
        project.setStage(ProjectStage.IN_DEVELOPMENT);
        project.setStatus(ProjectStatus.PENDING);
        project.setPositions(new HashSet<>());

        position = new ProjectPosition();
        position.setId(1L);
        position.setRoleName("Developer");
        position.setQuantity(2);
        position.setProject(project);

        createRequest = new CreateProjectRequest();
        createRequest.setName("Test Project");
        createRequest.setDescription("Test Description");
        createRequest.setSelectedCategory("Test Category");
        createRequest.setStage("IN_DEVELOPMENT");
    }

    // CREATE PROJECT TESTS

    @Test
    void createProject_ValidRequest_ShouldCreateProject() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(categoryRepository.findByName("Test Category")).thenReturn(Optional.of(category));
        when(projectMapper.toEntity(createRequest)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);

        ProjectResponseDto expectedDto = new ProjectResponseDto();
        expectedDto.setName("Test Project");
        when(projectMapper.toDto(project)).thenReturn(expectedDto);

        // Act
        ProjectResponseDto result = projectService.createProject(createRequest, "testuser");

        // Assert
        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectRepository).save(project);
        assertEquals(ProjectStatus.PENDING, project.getStatus());
        assertEquals(user, project.getCreator());
    }

    @Test
    void createProject_UserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.createProject(createRequest, "nonexistent")
        );

        assertEquals("User not found", exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void createProject_WithPositions_ShouldCreateProjectWithPositions() {
        // Arrange
        PositionRequest positionRequest = new PositionRequest();
        positionRequest.setRoleName("Developer");
        createRequest.setPositions(List.of(positionRequest));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(categoryRepository.findByName("Test Category")).thenReturn(Optional.of(category));
        when(projectMapper.toEntity(createRequest)).thenReturn(project);
        when(positionMapper.toEntity(positionRequest)).thenReturn(position);
        when(projectRepository.save(project)).thenReturn(project);

        ProjectResponseDto expectedDto = new ProjectResponseDto();
        when(projectMapper.toDto(project)).thenReturn(expectedDto);

        // Act
        projectService.createProject(createRequest, "testuser");

        // Assert
        assertEquals(1, project.getPositions().size());
        assertEquals(project, new ArrayList<>(project.getPositions()).get(0).getProject());
        verify(projectRepository).save(project);
    }

    @Test
    void createProject_WithNewCategory_ShouldCreateCategoryAndProject() {
        // Arrange
        createRequest.setSelectedCategory(null);
        createRequest.setCustomCategory("New Category");

        Category newCategory = new Category();
        newCategory.setName("New Category");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(categoryRepository.findByName("New Category")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);
        when(projectMapper.toEntity(createRequest)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);

        ProjectResponseDto expectedDto = new ProjectResponseDto();
        when(projectMapper.toDto(project)).thenReturn(expectedDto);

        // Act
        projectService.createProject(createRequest, "testuser");

        // Assert
        verify(categoryRepository).save(any(Category.class));
        verify(projectRepository).save(project);
    }

    @Test
    void createProject_InvalidStage_ShouldThrowException() {
        // Arrange
        createRequest.setStage("INVALID_STAGE");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(categoryRepository.findByName("Test Category")).thenReturn(Optional.of(category));


        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.createProject(createRequest, "testuser")
        );

        assertTrue(exception.getMessage().contains("Invalid project stage"),
                "Error should  mention invalid project stage");
        assertTrue(exception.getMessage().contains("[NOT_STARTED, IN_DEVELOPMENT, FINISHED, NEEDS_FIXES]"),
                "Error should list valid project stages");
    }

    // GET MY PROJECTS TESTS

    @Test
    void getMyProjects_ValidUser_ShouldReturnProjects() {
        // Arrange
        List<Project> userProjects = List.of(project);
        ProjectResponseDto expectedDto = new ProjectResponseDto();
        expectedDto.setName("Test Project");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(projectRepository.findByCreatorWithPositions(user)).thenReturn(userProjects);
        when(projectMapper.toDto(project)).thenReturn(expectedDto);

        // Act
        List<ProjectResponseDto> result = projectService.getMyProjects("testuser");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getName());
        verify(projectRepository).findByCreatorWithPositions(user);
    }

    @Test
    void getMyProjects_UserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.getMyProjects("nonexistent")
        );

        assertEquals("User not found", exception.getMessage());
        verify(projectRepository, never()).findByCreatorWithPositions(any(User.class));
    }

    // UPDATE PROJECT TESTS

    @Test
    void updateProject_ValidRequest_ShouldUpdateProject() {
        // Arrange
        createRequest.setName("Updated Project");
        createRequest.setDescription("Updated Description");

        when(projectRepository.findByIdWithPositions(1L)).thenReturn(Optional.of(project));
        when(categoryRepository.findByName("Test Category")).thenReturn(Optional.of(category));
        when(projectRepository.save(project)).thenReturn(project);

        ProjectResponseDto expectedDto = new ProjectResponseDto();
        expectedDto.setName("Updated Project");
        when(projectMapper.toDto(project)).thenReturn(expectedDto);

        // Act
        ProjectResponseDto result = projectService.updateProject(1L, createRequest, "testuser");

        // Assert
        assertEquals("Updated Project", project.getName());
        assertEquals("Updated Description", project.getDescription());
        assertEquals(ProjectStatus.PENDING, project.getStatus());
        verify(projectRepository).save(project);
    }

    @Test
    void updateProject_ProjectNotFound_ShouldThrowException() {
        // Arrange
        when(projectRepository.findByIdWithPositions(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.updateProject(1L, createRequest, "testuser")
        );

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void updateProject_UnauthorizedUser_ShouldThrowException() {
        // Arrange
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        project.setCreator(otherUser);

        when(projectRepository.findByIdWithPositions(1L)).thenReturn(Optional.of(project));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.updateProject(1L, createRequest, "testuser")
        );

        assertEquals("You are not allowed to update this project", exception.getMessage());
    }

    // DELETE PROJECT TESTS

    @Test
    void deleteProject_ValidRequest_ShouldDeleteProject() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        doNothing().when(projectRepository).delete(project);

        // Act
        projectService.deleteProject(1L, "testuser");

        // Assert
        verify(projectRepository).delete(project);
    }

    @Test
    void deleteProject_ProjectNotFound_ShouldThrowException() {
        // Arrange
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.deleteProject(1L, "testuser")
        );

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void deleteProject_UnauthorizedUser_ShouldThrowException() {
        // Arrange
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        project.setCreator(otherUser);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.deleteProject(1L, "testuser")
        );

        assertEquals("You are not allowed to delete this project", exception.getMessage());
    }

    // APPLY FOR POSITION TESTS

    @Test
    void applyForPosition_ValidRequest_ShouldCreateApplication() {
        // Arrange
        User applicant = new User();
        applicant.setId(2L);
        applicant.setUsername("applicant");

        ApplyForPositionRequest applyRequest = new ApplyForPositionRequest();

        when(userRepository.findByUsername("applicant")).thenReturn(Optional.of(applicant));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectPositionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(applicationRepository.existsByApplicantAndPosition(applicant, position)).thenReturn(false);

        // Act
        projectService.applyForPosition(1L, 1L, "applicant", applyRequest);

        // Assert
        verify(applicationRepository).save(any(PositionApplication.class));
        verify(projectPositionRepository).save(position);
        assertEquals(1, position.getQuantity()); // Quantity decreased from 2 to 1
    }

    @Test
    void applyForPosition_ApplicantNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.applyForPosition(1L, 1L, "nonexistent", new ApplyForPositionRequest())
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void applyForPosition_ProjectNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("applicant")).thenReturn(Optional.of(new User()));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.applyForPosition(1L, 1L, "applicant", new ApplyForPositionRequest())
        );

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void applyForPosition_PositionNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("applicant")).thenReturn(Optional.of(new User()));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectPositionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.applyForPosition(1L, 1L, "applicant", new ApplyForPositionRequest())
        );

        assertEquals("Position not found", exception.getMessage());
    }

    @Test
    void applyForPosition_PositionNotInProject_ShouldThrowException() {
        // Arrange
        Project otherProject = new Project();
        otherProject.setId(2L);

        ProjectPosition position = new ProjectPosition();
        position.setId(1L);
        position.setProject(otherProject);

        when(userRepository.findByUsername("applicant")).thenReturn(Optional.of(new User()));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectPositionRepository.findById(1L)).thenReturn(Optional.of(position));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.applyForPosition(1L, 1L, "applicant", new ApplyForPositionRequest())
        );

        assertEquals("Position does not belong to this project", exception.getMessage());
    }

    @Test
    void applyForPosition_OwnerApplying_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectPositionRepository.findById(1L)).thenReturn(Optional.of(position));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.applyForPosition(1L, 1L, "testuser", new ApplyForPositionRequest())
        );

        assertEquals("You cannot apply for your own project", exception.getMessage());
    }

    @Test
    void applyForPosition_NoAvailability_ShouldThrowException() {
        // Arrange
        User applicant = new User();
        applicant.setId(2L);
        applicant.setUsername("applicant");

        position.setQuantity(0);

        when(userRepository.findByUsername("applicant")).thenReturn(Optional.of(applicant));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectPositionRepository.findById(1L)).thenReturn(Optional.of(position));

        // Act & Assert
        assertThrows(PositionUnavailableException.class, () ->
                projectService.applyForPosition(1L, 1L, "applicant", new ApplyForPositionRequest())
        );
    }

    @Test
    void applyForPosition_MissingAnswer1_ShouldThrowException() {
        // Arrange
        User applicant = new User();
        applicant.setId(2L);
        applicant.setUsername("applicant");

        project.setQuestion1("Question 1?");
        ApplyForPositionRequest applyRequest = new ApplyForPositionRequest();
        // No answer1 provided

        when(userRepository.findByUsername("applicant")).thenReturn(Optional.of(applicant));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectPositionRepository.findById(1L)).thenReturn(Optional.of(position));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.applyForPosition(1L, 1L, "applicant", applyRequest)
        );

        assertEquals("You must provide an answer for question1", exception.getMessage());
    }

    @Test
    void applyForPosition_AlreadyApplied_ShouldThrowException() {
        // Arrange
        User applicant = new User();
        applicant.setId(2L);
        applicant.setUsername("applicant");

        ApplyForPositionRequest applyRequest = new ApplyForPositionRequest();

        when(userRepository.findByUsername("applicant")).thenReturn(Optional.of(applicant));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectPositionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(applicationRepository.existsByApplicantAndPosition(applicant, position)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.applyForPosition(1L, 1L, "applicant", applyRequest)
        );

        assertEquals("You have already applied for this position", exception.getMessage());
    }

    // GET PROJECTS USER APPLIED TO TESTS

    @Test
    void getProjectsUserAppliedTo_ValidUser_ShouldReturnProjects() {
        // Arrange
        User applicant = new User();
        applicant.setId(2L);
        applicant.setUsername("applicant");

        PositionApplication application = new PositionApplication();
        application.setStatus(ApplicationStatus.ACCEPTED);
        application.setFeedback("Great application!");
        application.setPosition(position);

        List<PositionApplication> applications = List.of(application);

        ProjectResponseDto expectedDto = new ProjectResponseDto();
        expectedDto.setName("Test Project");

        when(userRepository.findByUsername("applicant")).thenReturn(Optional.of(applicant));
        when(applicationRepository.findByApplicantWithProject(applicant)).thenReturn(applications);
        when(projectMapper.toDto(project)).thenReturn(expectedDto);

        // Act
        List<ProjectResponseDto> result = projectService.getProjectsUserAppliedTo("applicant");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getName());
        assertEquals("ACCEPTED", result.get(0).getApplicationStatus());
        assertEquals("Great application!", result.get(0).getFeedback());
    }

    // GET APPLICANTS FOR PROJECT TESTS

    @Test
    void getApplicantsForProject_ValidRequest_ShouldReturnApplicants() {
        // Arrange
        PositionApplication application = new PositionApplication();
        List<PositionApplication> applications = List.of(application);

        ApplicantResponseDto applicantDto = new ApplicantResponseDto();
        applicantDto.setApplicantUsername("applicant");

        when(projectRepository.findByIdWithPositions(1L)).thenReturn(Optional.of(project));
        when(applicationRepository.findByProject(project)).thenReturn(applications);
        when(applicantMapper.toDto(application)).thenReturn(applicantDto);

        // Act
        List<ApplicantResponseDto> result = projectService.getApplicantsForProject(1L, "testuser");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getApplicantsForProject_UnauthorizedUser_ShouldThrowException() {
        // Arrange
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        project.setCreator(otherUser);

        when(projectRepository.findByIdWithPositions(1L)).thenReturn(Optional.of(project));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.getApplicantsForProject(1L, "testuser")
        );

        assertEquals("You are not authorized to view applicants for this project", exception.getMessage());
    }

    // UPDATE APPLICATION STATUS TESTS

    @Test
    void updateApplicationStatus_Accept_ShouldUpdateStatus() {
        // Arrange
        ApplicationUpdateRequest request = new ApplicationUpdateRequest();
        request.setApplicationIds(List.of(1L));
        request.setAccept(true);
        request.setFeedback("Congratulations!");

        PositionApplication application = new PositionApplication();
        application.setId(1L);
        application.setPosition(position);
        List<PositionApplication> applications = List.of(application);

        when(applicationRepository.findAllById(request.getApplicationIds())).thenReturn(applications);

        // Act
        projectService.updateApplicationStatus("testuser", request);

        // Assert
        assertEquals(ApplicationStatus.ACCEPTED, application.getStatus());
        assertEquals("Congratulations!", application.getFeedback());
        verify(applicationRepository).saveAll(applications);
    }

    @Test
    void updateApplicationStatus_Reject_ShouldUpdateStatus() {
        // Arrange
        ApplicationUpdateRequest request = new ApplicationUpdateRequest();
        request.setApplicationIds(List.of(1L));
        request.setAccept(false);
        request.setFeedback("Sorry, not a good fit");

        PositionApplication application = new PositionApplication();
        application.setId(1L);
        application.setPosition(position);
        List<PositionApplication> applications = List.of(application);

        when(applicationRepository.findAllById(request.getApplicationIds())).thenReturn(applications);

        // Act
        projectService.updateApplicationStatus("testuser", request);

        // Assert
        assertEquals(ApplicationStatus.REJECTED, application.getStatus());
        assertEquals("Sorry, not a good fit", application.getFeedback());
    }

    @Test
    void updateApplicationStatus_UnauthorizedUser_ShouldThrowException() {
        // Arrange
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        project.setCreator(otherUser);

        ApplicationUpdateRequest request = new ApplicationUpdateRequest();
        request.setApplicationIds(List.of(1L));

        PositionApplication application = new PositionApplication();
        application.setId(1L);
        application.setPosition(position);
        List<PositionApplication> applications = List.of(application);

        when(applicationRepository.findAllById(request.getApplicationIds())).thenReturn(applications);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.updateApplicationStatus("testuser", request)
        );

        assertEquals("You are not authorized to update this application", exception.getMessage());
    }

    // ACCEPT PROJECT TESTS

    @Test
    void acceptProject_ValidRequest_ShouldAcceptProject() {
        // Arrange
        project.setStatus(ProjectStatus.PENDING);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        // Act
        projectService.acceptProject(1L, "admin");

        // Assert
        assertEquals(ProjectStatus.ACCEPTED, project.getStatus());
        verify(projectRepository).save(project);
    }

    @Test
    void acceptProject_NotPending_ShouldThrowException() {
        // Arrange
        project.setStatus(ProjectStatus.ACCEPTED);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.acceptProject(1L, "admin")
        );

        assertEquals("Only pending projects can be accepted", exception.getMessage());
    }

    // REJECT PROJECT TESTS

    @Test
    void rejectProject_ValidRequest_ShouldRejectProject() {
        // Arrange
        project.setStatus(ProjectStatus.PENDING);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        // Act
        projectService.rejectProject(1L, "admin", "Not suitable");

        // Assert
        assertEquals(ProjectStatus.REJECTED, project.getStatus());
        verify(projectRepository).save(project);
    }

    // GET PROJECTS BY STATUS TESTS

    @Test
    void getAcceptedProjects_ShouldReturnAcceptedProjects() {
        // Arrange
        List<Project> acceptedProjects = List.of(project);
        ProjectResponseDto expectedDto = new ProjectResponseDto();

        when(projectRepository.findByStatusWithPositions(ProjectStatus.ACCEPTED)).thenReturn(acceptedProjects);
        when(projectMapper.toDto(project)).thenReturn(expectedDto);

        // Act
        List<ProjectResponseDto> result = projectService.getAcceptedProjects();

        // Assert
        assertNotNull(result);
        verify(projectRepository).findByStatusWithPositions(ProjectStatus.ACCEPTED);
    }

    @Test
    void getProjectById_ValidId_ShouldReturnProject() {
        // Arrange
        ProjectResponseDto expectedDto = new ProjectResponseDto();
        expectedDto.setName("Test Project");

        when(projectRepository.findByIdWithPositions(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(expectedDto);

        // Act
        ProjectResponseDto result = projectService.getProjectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Project", result.getName());
    }

    @Test
    void getProjectById_InvalidId_ShouldThrowException() {
        // Arrange
        when(projectRepository.findByIdWithPositions(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                projectService.getProjectById(1L)
        );

        assertEquals("Project not found", exception.getMessage());
    }

    // GET ACCEPTED APPLICANTS TESTS

    @Test
    void getAcceptedApplicants_ValidRequest_ShouldReturnApplicants() {
        // Arrange
        User applicant = new User();
        applicant.setId(2L);
        applicant.setUsername("applicant");

        PositionApplication application = new PositionApplication();
        application.setApplicant(applicant);
        application.setStatus(ApplicationStatus.ACCEPTED);

        List<PositionApplication> acceptedApplications = List.of(application);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(applicationRepository.findByProjectAndStatus(project, ApplicationStatus.ACCEPTED))
                .thenReturn(acceptedApplications);

        // Act
        List<AcceptedApplicantDto> result = projectService.getAcceptedApplicants(1L, "testuser");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("applicant", result.get(0).getUsername());
        assertEquals(2L, result.get(0).getId());
    }
}