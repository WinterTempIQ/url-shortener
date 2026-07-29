package com.github.wintertempiq.urlshortener.user.service;

import com.github.wintertempiq.urlshortener.exceptions.EmailAlreadyExistsException;
import com.github.wintertempiq.urlshortener.exceptions.NotFoundException;
import com.github.wintertempiq.urlshortener.user.dto.NewUserDto;
import com.github.wintertempiq.urlshortener.user.dto.UserDto;
import com.github.wintertempiq.urlshortener.user.entity.User;
import com.github.wintertempiq.urlshortener.user.mapper.UserMapper;
import com.github.wintertempiq.urlshortener.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_ShouldReturnUserDto_WhenEmailIsUnique() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setEmail("test@test.test");
        newUserDto.setPassword("test_password_test");

        User userBeforeSave = new User();
        userBeforeSave.setEmail("test@test.test");
        userBeforeSave.setPassword("test_password_test");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@test.test");
        savedUser.setPassword("encoded_password");

        UserDto expectedUserDto = UserDto.builder()
                .id(1L)
                .email("test@test.test")
                .build();

        when(userRepository.existsByEmail("test@test.test")).thenReturn(false);
        when(userMapper.newUserDtoToUser(newUserDto)).thenReturn(userBeforeSave);
        when(passwordEncoder.encode("test_password_test")).thenReturn("encoded_password");
        when(userRepository.save(userBeforeSave)).thenReturn(savedUser);
        when(userMapper.userToUserDto(savedUser)).thenReturn(expectedUserDto);

        UserDto result = userService.registerUser(newUserDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@test.test");

        verify(userRepository).existsByEmail("test@test.test");
        verify(userMapper).newUserDtoToUser(newUserDto);
        verify(passwordEncoder).encode("test_password_test");
        verify(userRepository).save(userBeforeSave);
        verify(userMapper).userToUserDto(savedUser);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setEmail("existing@example.com");
        newUserDto.setPassword("password123");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(newUserDto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("existing@example.com");

        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).userToUserDto(any(User.class));
    }

    @Test
    void findUserById_ShouldReturnUserDto_WhenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.test");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail("test@test.test");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(dto);

        UserDto result = userService.findUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@test.test");

        verify(userMapper).userToUserDto(any(User.class));
    }

    @Test
    void findUserById_ShouldThrowException_NotFoundException() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден.");

        verify(userMapper, never()).userToUserDto(any(User.class));
    }

    @Test
    void findUserByEmail_ShouldReturnUserDto_WhenEmailExist() {
        String email = "test@test.test";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(dto);

        UserDto result = userService.findUserByEmail(email);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo(email);

        verify(userRepository).findByEmail(any(String.class));
        verify(userMapper).userToUserDto(any(User.class));
    }

    @Test
    void findUserByEmail_ShouldThrowException_NotFoundException() {

        when(userRepository.findByEmail("test@test.test")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserByEmail("test@test.test"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден.");

        verify(userMapper, never()).userToUserDto(any(User.class));
    }


}
