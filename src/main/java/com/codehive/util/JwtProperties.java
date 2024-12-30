package com.codehive.util;

import io.github.cdimascio.dotenv.Dotenv;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Getter
@Component
public class JwtProperties {

    @Value("${jwt.access.secretKey}")
    private  String accessTokenSecretKey;

    @Value("${jwt.refresh.secretKey}")
    private  String refreshTokenSecretKey;


    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;



}
