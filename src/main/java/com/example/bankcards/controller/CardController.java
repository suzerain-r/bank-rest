package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.payload.response.CardNumberResponse;
import com.example.bankcards.payload.request.TransferRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.payload.response.MessageResponse;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Карты", description = "Методы для пользователя с картами")
public class CardController {

    private final CardService cardService;
    private final UserRepository userRepository;

    @Operation(summary = "Получить все карты текущего пользователя")
    @GetMapping("/my")
    public ResponseEntity<List<CardDTO>> getMyCards(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return ResponseEntity.ok(cardService.getUserCards(user.getId()));
    }

    @Operation(summary = "Перевести деньги между картами")
    @PostMapping("/transfer")
    public ResponseEntity<MessageResponse> transfer(@RequestBody TransferRequest transferRequest) {
        cardService.transfer(transferRequest.getFromCardId(), transferRequest.getToCardId(), transferRequest.getAmount());
        return ResponseEntity.ok(new MessageResponse("Деньги успешно переведены!"));
    }

    @Operation(summary = "Запросить блокировку своей карты")
    @PutMapping("/{id}/block")
    public ResponseEntity<MessageResponse> requestBlock(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        cardService.requestBlock(id, user.getId());
        return ResponseEntity.ok(new MessageResponse("Карта заблокирована"));
    }

    @Operation(summary = "Получить полный номер карты по ID")
    @GetMapping("/{cardId}/number")
    public ResponseEntity<CardNumberResponse> getFullCardNumber(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.getFullCardNumber(cardId));
    }
}