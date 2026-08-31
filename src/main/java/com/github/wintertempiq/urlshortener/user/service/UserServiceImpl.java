package com.github.wintertempiq.urlshortener.user.service;

import com.github.wintertempiq.urlshortener.exceptions.EmailAlreadyExistsException;
import com.github.wintertempiq.urlshortener.exceptions.NotFoundException;
import com.github.wintertempiq.urlshortener.user.dto.NewUserDto;
import com.github.wintertempiq.urlshortener.user.dto.UserDto;
import com.github.wintertempiq.urlshortener.user.entity.User;
import com.github.wintertempiq.urlshortener.user.mapper.UserMapper;
import com.github.wintertempiq.urlshortener.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerUser(NewUserDto dto) {
        log.info("User registration attempt via email: {}", dto.getEmail());

        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Registration is interrupted, email is not unique: {}", dto.getEmail());
            throw new EmailAlreadyExistsException("Failed to register.");
        }

        User user = userMapper.newUserDtoToUser(dto);

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User saveUser = userRepository.save(user);

        log.info("Successful user registration with email: {}", dto.getEmail());
        return userMapper.userToUserDto(saveUser);
    }

    @Override
    public UserDto findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found by email lookup");
                    return new NotFoundException("User not found.");
                });

        return userMapper.userToUserDto(user);
    }

    @Override
    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }


}
