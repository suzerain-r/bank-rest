package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.payload.request.CreateCardRequest;
import com.example.bankcards.payload.response.MessageResponse;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Админ", description = "Методы управления картами и пользователями для администратора")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final CardService cardService;
    private final UserService userService;

    @Operation(summary = "Создать карту")
    @PostMapping("/cards/create")
    public ResponseEntity<CardDTO> createCard(@RequestBody CreateCardRequest dto) {
        return ResponseEntity.ok(cardService.createCard(dto));
    }

    @Operation(summary = "Заблокировать карту по ID")
    @PutMapping("/cards/{id}/block")
    public ResponseEntity<MessageResponse> blockCard(@PathVariable Long id) {
        cardService.blockByAdmin(id);
        return ResponseEntity.ok(new MessageResponse("Карта заблокирована"));
    }

    @Operation(summary = "Удалить карту по ID")
    @DeleteMapping("/cards/{id}")
    public ResponseEntity<MessageResponse> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.ok(new MessageResponse("Карта была удалена"));
    }

    @Operation(summary = "Удалить пользователя по ID")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("Пользователь был удален"));
    }


    @Operation(summary = "Получить список всех карт")
    @GetMapping("/cards")
    public ResponseEntity<List<CardDTO>> getAllCards() {
        return ResponseEntity.ok(cardService.getAllCards());
    }

    @Operation(summary = "Получить список всех пользователей")
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
