package com.codehive.util;

import io.github.cdimascio.dotenv.Dotenv;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Getter
@Component
public class JwtProperties {

    private final String accessTokenSecretKey;
    private final String refreshTokenSecretKey;


    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    public JwtProperties() {
        Dotenv dotenv = Dotenv.load();
        this.accessTokenSecretKey = dotenv.get("JWT_ACCESS_SECRET_KEY");
        this.refreshTokenSecretKey = dotenv.get("JWT_REFRESH_SECRET");
    }


}
