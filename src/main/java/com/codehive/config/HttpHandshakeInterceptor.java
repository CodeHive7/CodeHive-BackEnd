package com.codehive.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            // Extract token from URL parameter
            String token = httpServletRequest.getParameter("token");

            if((token == null || token.isEmpty()) && httpServletRequest.getHeader("Authorization") != null) {
                String authHeader = httpServletRequest.getHeader("Authorization");
                if (authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }

            if (token != null && !token.isEmpty()) {
                attributes.put("token", token);
                return true;
            }
        }
        System.out.println("WebSocket handshake rejected - no valid token found");
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception ex) {
        // Nothing to do after handshake
    }
}
