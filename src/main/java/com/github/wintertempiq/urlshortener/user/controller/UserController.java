package com.github.wintertempiq.urlshortener.user.controller;

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

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody NewUserDto dto) {
        return service.registerUser(dto);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return service.findUserById(id);
    }

    @GetMapping("/me")
    public UserDto getCurrentUser() {
        // Пока заглушка, потом возьмем email из SecurityContext
        return null;
    }

}
