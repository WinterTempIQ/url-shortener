package com.github.wintertempiq.urlshortener.security;

import com.github.wintertempiq.urlshortener.exceptions.BadCredentialsException;
import com.github.wintertempiq.urlshortener.user.dto.JwtResponseDto;
import com.github.wintertempiq.urlshortener.user.dto.LoginRequestDto;
import com.github.wintertempiq.urlshortener.user.entity.User;
import com.github.wintertempiq.urlshortener.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtResponseDto authenticate(LoginRequestDto request) {
        log.info("Attempting authentication for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Authentication failed - user not found");
                    return new BadCredentialsException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Authentication failed - invalid password.");
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());

        log.info("Authentication successful for email: {}", request.getEmail());
        return new JwtResponseDto(token, user.getEmail());
    }
}
