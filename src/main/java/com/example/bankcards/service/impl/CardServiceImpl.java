package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.payload.CreateCardRequestDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepo;
    private final UserRepository userRepo;

    @Override
    public CardDTO createCard(CreateCardRequestDTO dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card card = new Card();
        card.setOwnerName(dto.getOwnerName());
        card.setExpiryDate(LocalDate.now().plusYears(2));
        card.setBalance(BigDecimal.ZERO);
        card.setUser(user);
        card.setStatus(CardStatus.ACTIVE);

        String cardNumber = generateCardNumber();
        String encrypted = encrypt(cardNumber);
        card.setEncryptedCardNumber(cardNumber);

        Card saved = cardRepo.save(card);
        return CardMapper.toDto(saved);
    }

    @Override
    public List<CardDTO> getUserCards(Long userId) {
        return cardRepo.findByUserId(userId).stream()
                .map(CardMapper::toDto)
                .toList();
    }

    @Override
    public void requestBlock(Long cardId, Long userId) {
        Card card = cardRepo.findById(cardId).orElseThrow();
        if (!card.getUser().getId().equals(userId)) throw new AccessDeniedException("Forbidden");
        card.setStatus(CardStatus.BLOCKED);
        cardRepo.save(card);
    }

    @Override
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Card from = cardRepo.findById(fromId).orElseThrow();
        Card to = cardRepo.findById(toId).orElseThrow();
        if (from.getBalance().compareTo(amount) < 0) throw new RuntimeException("Insufficient balance");
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        cardRepo.save(from);
        cardRepo.save(to);
    }

    @Override
    public void blockByAdmin(Long cardId) {
        Card card = cardRepo.findById(cardId).orElseThrow();
        card.setStatus(CardStatus.BLOCKED);
        cardRepo.save(card);
    }

    @Override
    public void deleteCard(Long cardId) {
        cardRepo.deleteById(cardId);
    }

    @Override
    public List<CardDTO> getAllCards() {
        return cardRepo.findAll().stream()
                .map(CardMapper::toDto)
                .toList();
    }

    private String generateCardNumber() {
        return UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 16);
    }

    private String encrypt(String number) {
        return Base64.getEncoder().encodeToString(number.getBytes());
    }

    private String mask(String encrypted) {
        String decoded = new String(Base64.getDecoder().decode(encrypted));
        return "**** **** **** " + decoded.substring(12);
    }
}
