package com.github.wintertempiq.urlshortener.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wintertempiq.urlshortener.exceptions.EmailAlreadyExistsException;
import com.github.wintertempiq.urlshortener.exceptions.NotFoundException;
import com.github.wintertempiq.urlshortener.ratelimit.KeyExtractor;
import com.github.wintertempiq.urlshortener.ratelimit.RateLimiter;
import com.github.wintertempiq.urlshortener.security.JwtTokenProvider;
import com.github.wintertempiq.urlshortener.security.UserContext;
import com.github.wintertempiq.urlshortener.user.controller.UserController;
import com.github.wintertempiq.urlshortener.user.dto.NewUserDto;
import com.github.wintertempiq.urlshortener.user.dto.UserDto;
import com.github.wintertempiq.urlshortener.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserContext userContext;

    @MockitoBean
    private RateLimiter rateLimiter;

    @MockitoBean
    private KeyExtractor keyExtractor;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void register_shouldReturn201_whenUserCreated() throws Exception {
        NewUserDto newUserDto = NewUserDto.builder()
                .email("email@ma.com")
                .password("password")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .email(newUserDto.getEmail())
                .build();

        when(userService.registerUser(any(NewUserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email@ma.com"));

        verify(userService, times(1)).registerUser(any(NewUserDto.class));
    }

    @Test
    void register_shouldReturn400_whenInvalidCredentials() throws Exception {
        NewUserDto newUserDto = NewUserDto.builder()
                .email("email")
                .password("")
                .build();

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void register_shouldReturn409_whenFailedRegister() throws Exception {
        NewUserDto newUserDto = NewUserDto.builder()
                .email("email@ma.com")
                .password("password")
                .build();

        when(userService.registerUser(any(NewUserDto.class)))
                .thenThrow(new EmailAlreadyExistsException("Failed to register."));

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Failed to register."));
    }

    @Test
    void getCurrentUser_shouldReturn200_whenUserFind() throws Exception {
        String email = "email@emo.com";

        UserDto dto = UserDto.builder()
                .id(1L)
                .email(email)
                .build();

        when(userContext.getCurrentUserEmail()).thenReturn(email);
        when(userService.findUserByEmail(email)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value(email));

        verify(userService, times(1)).findUserByEmail(email);
    }

    @Test
    void getCurrentUser_shouldReturn404_whenUserNotFound() throws Exception {
        String email = "email@emo.com";

        when(userContext.getCurrentUserEmail()).thenReturn(email);
        when(userService.findUserByEmail(email))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(userContext, times(1)).getCurrentUserEmail();
        verify(userService, times(1)).findUserByEmail(email);
    }
}
