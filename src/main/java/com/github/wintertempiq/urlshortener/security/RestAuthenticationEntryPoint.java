package com.github.wintertempiq.urlshortener.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wintertempiq.urlshortener.exceptions.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    private final int status = HttpStatus.UNAUTHORIZED.value();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ApiError apiError = ApiError.builder()
                .status(status)
                .reason("Authentication required")
                .message("Authentication is required to access this resource")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        response.setStatus(status);
        response.setContentType("application/json");

        objectMapper.writeValue(response.getWriter(), apiError);
    }
}
