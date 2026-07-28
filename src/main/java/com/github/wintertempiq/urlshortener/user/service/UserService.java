package com.github.wintertempiq.urlshortener.user.service;

import com.github.wintertempiq.urlshortener.user.dto.NewUserDto;
import com.github.wintertempiq.urlshortener.user.dto.UserDto;

public interface UserService {

    UserDto registerUser(NewUserDto dto);

    UserDto findUserById(Long id);

    UserDto findUserByEmail(String email);
}
