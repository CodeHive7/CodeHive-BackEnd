package com.codehive.service.impl;

import com.codehive.dto.LoginRequest;
import com.codehive.dto.RegisterRequest;
import com.codehive.dto.TokenResponse;
import com.codehive.entity.Role;
import com.codehive.entity.User;
import com.codehive.repository.RoleRepository;
import com.codehive.repository.UserRepository;
import com.codehive.security.JwtTokenProvider;
import com.codehive.service.RefreshTokenService;
import com.codehive.util.JwtProperties;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        String secureAccessKey = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded());
        String secureRefreshKey = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded());

        when(jwtProperties.getAccessTokenExpiration()).thenReturn(3600000L);
        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(86400000L);
        when(jwtProperties.getAccessTokenSecretKey()).thenReturn(secureAccessKey);
        when(jwtProperties.getRefreshTokenSecretKey()).thenReturn(secureRefreshKey);
        when(jwtTokenProvider.getJwtProperties()).thenReturn(jwtProperties);
    }

    @Test
    void register_ValidRequest_ShouldRegisterUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setFullName("Test User");
        request.setEmail("test@example.com");

        Role userRole = new Role();
        userRole.setName("USER");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        String result = authService.register(request);

        assertEquals("User registered successfully", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_DuplicateUsername_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));

        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void register_MissingRole_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));

        assertEquals("Role is not found", exception.getMessage());
    }

    @Test
    void authenticate_ValidCredentials_ShouldReturnToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateAccessToken("testuser")).thenReturn("accessToken");

        String token = authService.authenticate(request);

        assertEquals("accessToken", token);
    }

    @Test
    void login_ValidCredentials_ShouldReturnTokens() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        User user = new User();
        user.setUsername("testuser");
        user.setActive(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken("testuser")).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken("testuser")).thenReturn("refreshToken");

        TokenResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    void login_BlockedUser_ShouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");

        User user = new User();
        user.setUsername("testuser");
        user.setActive(false);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));

        assertEquals("User is blocked", exception.getMessage());
    }

    @Test
    void jwtToken_ValidateToken_ShouldPass() {
        String token = "validToken";
        when(jwtTokenProvider.validateToken(token, true)).thenReturn(true);

        boolean isValid = jwtTokenProvider.validateToken(token, true);

        assertTrue(isValid);
    }

    @Test
    void jwtToken_InvalidToken_ShouldFail() {
        String token = "invalidToken";
        when(jwtTokenProvider.validateToken(token, false)).thenReturn(false);

        boolean isValid = jwtTokenProvider.validateToken(token, false);

        assertFalse(isValid);
    }
}