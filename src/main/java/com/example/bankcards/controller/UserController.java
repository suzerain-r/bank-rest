package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Методы получения информации о пользователях")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Получить данные текущего пользователя")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getProfile() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

}