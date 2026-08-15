package com.github.wintertempiq.urlshortener.user.service;

import com.github.wintertempiq.urlshortener.user.dto.NewUserDto;
import com.github.wintertempiq.urlshortener.user.dto.UserDto;
import com.github.wintertempiq.urlshortener.user.entity.User;

public interface UserService {

    UserDto registerUser(NewUserDto dto);

    UserDto findUserByEmail(String email);

    User getUserEntityByEmail(String email);
}
