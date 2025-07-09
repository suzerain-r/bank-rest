package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.payload.TransferRequestDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final UserRepository userRepository;


    @GetMapping("/my")
    public ResponseEntity<List<CardDTO>> getMyCards(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(cardService.getUserCards(user.getId()));
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody TransferRequestDTO transferRequestDTO) {
        cardService.transfer(transferRequestDTO.getFromCardId(), transferRequestDTO.getToCardId(), transferRequestDTO.getAmount());
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<Void> requestBlock(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        cardService.requestBlock(id, user.getId());
        return ResponseEntity.ok().build();
    }
}