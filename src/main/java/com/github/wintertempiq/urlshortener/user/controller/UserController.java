package com.github.wintertempiq.urlshortener.user.controller;

import com.github.wintertempiq.urlshortener.security.UserContext;
import com.github.wintertempiq.urlshortener.user.dto.NewUserDto;
import com.github.wintertempiq.urlshortener.user.dto.UserDto;
import com.github.wintertempiq.urlshortener.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserContext context;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody NewUserDto dto) {
        return service.registerUser(dto);
    }

    @GetMapping("/me")
    public UserDto getCurrentUser() {
        return service.findUserByEmail(context.getCurrentUserEmail());
    }

}
