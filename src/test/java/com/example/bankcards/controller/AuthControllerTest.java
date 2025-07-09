package com.example.bankcards.controller;

import com.example.bankcards.config.TestSecurityConfig;
import com.example.bankcards.entity.User;
import com.example.bankcards.payload.request.AuthRequest;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void registerNewUser_Success() throws Exception {
        AuthRequest authRequest = new AuthRequest("user1", "pass123");

        Mockito.when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode("pass123")).thenReturn("encoded-pass");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("Успешно зарегистрирован")));
    }

    @Test
    void registerUserAlreadyExists() throws Exception {
        AuthRequest authRequest = new AuthRequest("user1", "pass123");

        Mockito.when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionMessage").value("Пользователь уже существует"));
    }

    @Test
    void login_Success() throws Exception {
        AuthRequest authRequest = new AuthRequest("user1", "pass123");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("user1")
                .password("encoded")
                .roles("USER")
                .build();

        Mockito.when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mockito.mock(Authentication.class));

        Mockito.when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
        Mockito.when(jwtService.generateToken(userDetails)).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    void login_BadCredentials() throws Exception {
        AuthRequest authRequest = new AuthRequest("user1", "wrongpass");

        Mockito.when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionMessage").value("Неправильное имя пользователя или пароль"));
    }
}
