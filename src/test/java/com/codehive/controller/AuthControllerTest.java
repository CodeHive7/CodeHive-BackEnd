package com.codehive.controller;

  import com.codehive.dto.LoginRequest;
  import com.codehive.dto.RegisterRequest;
  import com.codehive.dto.TokenBody;
  import com.codehive.dto.TokenResponse;
  import com.codehive.entity.RefreshToken;
  import com.codehive.exception.BadRequestException;
  import com.codehive.exception.UserAlreadyExistsException;
  import com.codehive.security.JwtTokenProvider;
  import com.codehive.service.AuthService;
  import com.codehive.service.RefreshTokenService;
  import org.junit.jupiter.api.BeforeEach;
  import org.junit.jupiter.api.Test;
  import org.mockito.InjectMocks;
  import org.mockito.Mock;
  import org.mockito.MockitoAnnotations;
  import org.springframework.http.HttpStatus;
  import org.springframework.http.ResponseEntity;
  import org.springframework.security.authentication.BadCredentialsException;

  import java.util.HashMap;
  import java.util.Map;
  import java.util.Optional;

  import static org.junit.jupiter.api.Assertions.*;
  import static org.mockito.ArgumentMatchers.any;
  import static org.mockito.ArgumentMatchers.anyString;
  import static org.mockito.Mockito.*;

  class AuthControllerTest {

      @Mock
      private AuthService authService;

      @Mock
      private JwtTokenProvider jwtTokenProvider;

      @Mock
      private RefreshTokenService refreshTokenService;

      @InjectMocks
      private AuthController authController;

      @BeforeEach
      void setUp() {
          MockitoAnnotations.openMocks(this);
      }

      // REGISTER ENDPOINT TESTS

      @Test
      void register_ValidRequest_ShouldReturnSuccessMessage() {
          // Arrange
          RegisterRequest request = new RegisterRequest();
          request.setUsername("testuser");
          request.setPassword("password");
          request.setFullName("Test User");
          request.setEmail("test@example.com");

          when(authService.register(any(RegisterRequest.class))).thenReturn("User registered successfully");

          // Act
          ResponseEntity<String> response = authController.register(request);

          // Assert
          assertEquals(HttpStatus.OK, response.getStatusCode());
          assertEquals("User registered successfully", response.getBody());
          verify(authService).register(request);
      }

      @Test
      void register_DuplicateUsername_ShouldHandleException() {
          // Arrange
          RegisterRequest request = new RegisterRequest();
          request.setUsername("existinguser");
          request.setPassword("password");
          request.setFullName("Existing User");
          request.setEmail("existing@example.com");

          when(authService.register(any(RegisterRequest.class)))
                  .thenThrow(new UserAlreadyExistsException("Username already exists"));

          // Act & Assert
          Exception exception = assertThrows(UserAlreadyExistsException.class, () -> {
              authController.register(request);
          });

          assertEquals("Username already exists", exception.getMessage());
      }

      // LOGIN ENDPOINT TESTS

      @Test
      void login_ValidCredentials_ShouldReturnTokens() {
          // Arrange
          LoginRequest request = new LoginRequest();
          request.setUsername("testuser");
          request.setPassword("password");

          TokenResponse tokenResponse = new TokenResponse("access-token-value", "refresh-token-value");
          when(authService.login(any(LoginRequest.class))).thenReturn(tokenResponse);

          // Act
          ResponseEntity<TokenResponse> response = authController.login(request);

          // Assert
          assertEquals(HttpStatus.OK, response.getStatusCode());
          assertNotNull(response.getBody());
          assertEquals("access-token-value", response.getBody().getAccessToken());
          assertEquals("refresh-token-value", response.getBody().getRefreshToken());
          verify(authService).login(request);
      }

      @Test
      void login_InvalidCredentials_ShouldHandleException() {
          // Arrange
          LoginRequest request = new LoginRequest();
          request.setUsername("wronguser");
          request.setPassword("wrongpass");

          when(authService.login(any(LoginRequest.class)))
                  .thenThrow(new BadCredentialsException("Invalid username or password"));

          // Act & Assert
          Exception exception = assertThrows(BadCredentialsException.class, () -> {
              authController.login(request);
          });

          assertEquals("Invalid username or password", exception.getMessage());
      }

      // LOGOUT ENDPOINT TESTS

      @Test
      void logout_ValidToken_ShouldReturnSuccessMessage() {
          // Arrange
          TokenBody tokenBody = new TokenBody();
          tokenBody.setRefreshToken("valid-refresh-token");

          doNothing().when(refreshTokenService).revokeToken(anyString());

          // Act
          ResponseEntity<Map<String, String>> response = authController.logout(tokenBody);

          // Assert
          assertEquals(HttpStatus.OK, response.getStatusCode());
          assertNotNull(response.getBody());
          assertEquals("Logout successful", response.getBody().get("message"));
          verify(refreshTokenService).revokeToken("valid-refresh-token");
      }

      @Test
      void logout_NonExistentToken_StillReturnsSuccess() {
          // Arrange
          TokenBody tokenBody = new TokenBody();
          tokenBody.setRefreshToken("non-existent-token");

          doThrow(new RuntimeException("Token not found")).when(refreshTokenService).revokeToken("non-existent-token");

          // Act & Assert
          Exception exception = assertThrows(RuntimeException.class, () -> {
              authController.logout(tokenBody);
          });

          assertEquals("Token not found", exception.getMessage());
      }

      @Test
      void logout_NullToken_HandlesNullPointerGracefully() {
          // Arrange
          TokenBody tokenBody = new TokenBody();
          // refreshToken is null

          doThrow(new NullPointerException()).when(refreshTokenService).revokeToken(null);

          // Act & Assert
          assertThrows(NullPointerException.class, () -> {
              authController.logout(tokenBody);
          });
      }

      // REFRESH TOKEN ENDPOINT TESTS

      @Test
      void refresh_ValidToken_ShouldReturnNewTokens() {
          // Arrange
          Map<String, String> request = new HashMap<>();
          request.put("refreshToken", "valid-refresh-token");

          RefreshToken refreshTokenEntity = new RefreshToken();
          refreshTokenEntity.setToken("valid-refresh-token");

          when(refreshTokenService.findByToken("valid-refresh-token")).thenReturn(Optional.of(refreshTokenEntity));
          when(refreshTokenService.isValid(any(RefreshToken.class))).thenReturn(true);
          when(jwtTokenProvider.validateToken("valid-refresh-token", true)).thenReturn(true);
          when(jwtTokenProvider.getUsernameFromToken("valid-refresh-token", true)).thenReturn("testuser");
          when(jwtTokenProvider.generateRefreshToken("testuser")).thenReturn("new-refresh-token");
          when(jwtTokenProvider.generateAccessToken("testuser")).thenReturn("new-access-token");

          // Act
          ResponseEntity<TokenResponse> response = authController.refresh(request);

          // Assert
          assertEquals(HttpStatus.OK, response.getStatusCode());
          assertNotNull(response.getBody());
          assertEquals("new-access-token", response.getBody().getAccessToken());
          assertEquals("new-refresh-token", response.getBody().getRefreshToken());
          verify(refreshTokenService).updateRefreshToken(refreshTokenEntity, "new-refresh-token");
      }

      @Test
      void refresh_TokenNotFound_ShouldReturnUnauthorized() {
          // Arrange
          Map<String, String> request = new HashMap<>();
          request.put("refreshToken", "non-existent-token");

          when(refreshTokenService.findByToken("non-existent-token")).thenReturn(Optional.empty());

          // Act
          ResponseEntity<TokenResponse> response = authController.refresh(request);

          // Assert
          assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
          assertNull(response.getBody());
          verify(refreshTokenService, never()).updateRefreshToken(any(), anyString());
      }

      @Test
      void refresh_InvalidToken_ShouldReturnUnauthorized() {
          // Arrange
          Map<String, String> request = new HashMap<>();
          request.put("refreshToken", "invalid-token");

          RefreshToken refreshTokenEntity = new RefreshToken();
          refreshTokenEntity.setToken("invalid-token");

          when(refreshTokenService.findByToken("invalid-token")).thenReturn(Optional.of(refreshTokenEntity));
          when(refreshTokenService.isValid(refreshTokenEntity)).thenReturn(false);

          // Act
          ResponseEntity<TokenResponse> response = authController.refresh(request);

          // Assert
          assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
          assertNull(response.getBody());
          verify(refreshTokenService, never()).updateRefreshToken(any(), anyString());
      }

      @Test
      void refresh_JwtValidationFails_ShouldReturnUnauthorized() {
          // Arrange
          Map<String, String> request = new HashMap<>();
          request.put("refreshToken", "tampered-token");

          RefreshToken refreshTokenEntity = new RefreshToken();
          refreshTokenEntity.setToken("tampered-token");

          when(refreshTokenService.findByToken("tampered-token")).thenReturn(Optional.of(refreshTokenEntity));
          when(refreshTokenService.isValid(refreshTokenEntity)).thenReturn(true);
          when(jwtTokenProvider.validateToken("tampered-token", true)).thenReturn(false);

          // Act
          ResponseEntity<TokenResponse> response = authController.refresh(request);

          // Assert
          assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
          assertNull(response.getBody());
          verify(jwtTokenProvider, never()).getUsernameFromToken(anyString(), anyBoolean());
      }

      @Test
      void refresh_MissingRefreshTokenKey_ShouldHandleGracefully() {
          // Arrange
          Map<String, String> request = new HashMap<>();
          // Missing refreshToken key

          // Act & Assert
          assertThrows(BadRequestException.class, () -> {
              authController.refresh(request);
          });

          verify(refreshTokenService, never()).findByToken(anyString());
      }
  }