package com.codehive.security;

import com.codehive.entity.Permissions;
import com.codehive.entity.Role;
import com.codehive.entity.User;
import com.codehive.repository.UserRepository;
import com.codehive.util.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {


    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;


    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
    }

    public String generateAccessToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        List<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream().map(Permissions::getName))
                .distinct()
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpiration()))
                .signWith(getSigningKey(jwtProperties.getAccessTokenSecretKey()), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpiration()))
                .signWith(getSigningKey(jwtProperties.getRefreshTokenSecretKey()), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token, boolean isRefresh) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(isRefresh ? jwtProperties.getRefreshTokenSecretKey() : jwtProperties.getAccessTokenSecretKey()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token , boolean isRefresh) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(isRefresh ? jwtProperties.getRefreshTokenSecretKey() : jwtProperties.getAccessTokenSecretKey()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
