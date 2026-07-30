package com.github.wintertempiq.urlshortener.security;

import com.github.wintertempiq.urlshortener.user.dto.JwtResponseDto;
import com.github.wintertempiq.urlshortener.user.dto.LoginRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public JwtResponseDto login(@Valid @RequestBody LoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
