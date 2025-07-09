package com.example.bankcards.controller;

import com.example.bankcards.exception.ValidationException;
import com.example.bankcards.payload.request.AuthRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.URole;
import com.example.bankcards.payload.response.MessageResponse;
import com.example.bankcards.payload.response.TokenResponse;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.security.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Регистрация и вход пользователей")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;


    @Operation(summary = "Регистрация нового пользователя")
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody AuthRequest authRequest) {
        if (userRepository.findByUsername(authRequest.getUsername()).isPresent()) {
            throw new ValidationException("Пользователь уже существует");
        }

        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRole(URole.USER);

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Успешно зарегистрирован!"));
    }


    @Operation(summary = "Вход пользователя и получение JWT токена")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody AuthRequest authRequest) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new ValidationException("Неправильное имя пользователя или пароль");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        return ResponseEntity.ok(new TokenResponse(jwtService.generateToken(userDetails)));
    }
}

