//package com.codehive.controller;

import com.codehive.dto.LoginRequest;
import com.codehive.dto.RegisterRequest;
import com.codehive.dto.TokenBody;
import com.codehive.dto.TokenResponse;
import com.codehive.entity.RefreshToken;
import com.codehive.security.JwtTokenProvider;
import com.codehive.service.AuthService;
import com.codehive.service.RefreshTokenService;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//class AuthControllerTest {

  //  @Mock
    //private AuthService authService;

    //  @Mock
//    private JwtTokenProvider jwtTokenProvider;

  //  @Mock
    //private RefreshTokenService refreshTokenService;

    //@InjectMocks
    //private AuthController authController;

    //@BeforeEach
    //void setUp() {
      //  MockitoAnnotations.openMocks(this);
    //}

   // @Test
    //void register_ValidRequest_ShouldReturnSuccessMessage() {
      //  RegisterRequest request = new RegisterRequest();
        //request.setUsername("testuser");
        //request.setPassword("password");
        //request.setFullName("Test User");
        //request.setEmail("test@example.com");

        //when(authService.register(any(RegisterRequest.class))).thenReturn("User registered successfully");
        //ResponseEntity<String> response = authController.register(request);

        //assertEquals(HttpStatus.OK, response.getStatusCode());
        //assertEquals("User registered successfully", response.getBody());
   // }

    //@Test
    //void login_ValidCredentials_ShouldReturnTokens() {
     //   LoginRequest request = new LoginRequest();
       // request.setUsername("testuser");
        //request.setPassword("password");

        //TokenResponse tokenResponse = new TokenResponse("accessToken", "refreshToken");
        //when(authService.login(any(LoginRequest.class))).thenReturn(tokenResponse);

        //ResponseEntity<TokenResponse> response = authController.login(request);


        //assertEquals(HttpStatus.OK, response.getStatusCode());
        //assertNotNull(response.getBody());
        //assertEquals("accessToken", response.getBody().getAccessToken());
        //assertEquals("refreshToken", response.getBody().getRefreshToken());
    //}

    //@Test
   // void logout_ValidToken_ShouldReturnSuccessMessage() {
     //   TokenBody tokenBody = new TokenBody();
      //  tokenBody.setRefreshToken("validRefreshToken");

       // doNothing().when(refreshTokenService).revokeToken(anyString());

      //  ResponseEntity<Map<String, String>> response = authController.logout(tokenBody);

      //  assertEquals(HttpStatus.OK, response.getStatusCode());
    //    assertNotNull(response.getBody());
      //  assertEquals("Logout successful", response.getBody().get("message"));
  //  }

    //@Test
    //void refresh_ValidToken_ShouldReturnNewTokens() {
      //  String refreshToken = "validRefreshToken";
       // TokenResponse tokenResponse = new TokenResponse("newAccessToken", "newRefreshToken");

        //RefreshToken refreshTokenEntity = new RefreshToken();
        //refreshTokenEntity.setToken(refreshToken);

        //when(refreshTokenService.findByToken(anyString())).thenReturn(Optional.of(refreshTokenEntity));
//        when(refreshTokenService.isValid(any(RefreshToken.class))).thenReturn(true);
//        when(jwtTokenProvider.validateToken(anyString(), eq(true))).thenReturn(true);
//        when(jwtTokenProvider.getUsernameFromToken(anyString(), eq(true))).thenReturn("testuser");
//        when(jwtTokenProvider.generateRefreshToken(anyString())).thenReturn("newRefreshToken");
//        when(jwtTokenProvider.generateAccessToken(anyString())).thenReturn("newAccessToken");
//
//        ResponseEntity<TokenResponse> response = authController.refresh(refreshToken);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("newAccessToken", response.getBody().getAccessToken());
//        assertEquals("newRefreshToken", response.getBody().getRefreshToken());
//    }

//    @Test
//    void refresh_InvalidToken_ShouldReturnUnauthorized() {
//        String refreshToken = "invalidRefreshToken";
//
//        when(refreshTokenService.findByToken(anyString())).thenReturn(Optional.empty());
//
//        ResponseEntity<TokenResponse> response = authController.refresh(refreshToken);
//
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//        assertNull(response.getBody());
//    }

//    @Test
//    void refresh_ExpiredToken_ShouldReturnUnauthorized() {
//        String refreshToken = "expiredRefreshToken";
//
//        RefreshToken refreshTokenEntity = new RefreshToken();
//        refreshTokenEntity.setToken(refreshToken);
//
//        when(refreshTokenService.findByToken(anyString())).thenReturn(Optional.of(refreshTokenEntity));
//        when(refreshTokenService.isValid(any(RefreshToken.class))).thenReturn(false);
//
//        ResponseEntity<TokenResponse> response = authController.refresh(refreshToken);
//
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//
//    @Test
//    void refresh_ExpiredAccessToken_ShouldNotAffectRefreshToken() {
//        String refreshToken = "validRefreshToken";

//        RefreshToken refreshTokenEntity = new RefreshToken();
//        refreshTokenEntity.setToken(refreshToken);
//
//        when(refreshTokenService.findByToken(refreshToken)).thenReturn(Optional.of(refreshTokenEntity));
//        when(refreshTokenService.isValid(refreshTokenEntity)).thenReturn(true);
//        when(jwtTokenProvider.validateToken(refreshToken, true)).thenReturn(true);
//        when(jwtTokenProvider.getUsernameFromToken(refreshToken, true)).thenReturn("testuser");
//        when(jwtTokenProvider.generateRefreshToken("testuser")).thenReturn("newRefreshToken");
//        when(jwtTokenProvider.generateAccessToken("testuser")).thenReturn("newAccessToken");
//
//        ResponseEntity<TokenResponse> response = authController.refresh(refreshToken);

//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("newAccessToken", response.getBody().getAccessToken());
//        assertEquals("newRefreshToken", response.getBody().getRefreshToken());
//    }
//
//}