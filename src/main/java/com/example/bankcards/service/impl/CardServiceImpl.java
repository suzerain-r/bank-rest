package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.ValidationException;
import com.example.bankcards.payload.request.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.payload.response.CardNumberResponse;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepo;
    private final UserRepository userRepo;

    @Override
    public CardDTO createCard(CreateCardRequest dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Card card = Card.builder()
                .ownerName(dto.getOwnerName())
                .expiryDate(LocalDate.now().plusYears(2))
                .balance(BigDecimal.ZERO)
                .user(user)
                .status(CardStatus.ACTIVE)
                .build();

        String cardNumber = Util.generateCardNumber();
        String encrypted = Util.encrypt(cardNumber);
        card.setEncryptedCardNumber(encrypted);

        Card saved = cardRepo.save(card);
        return CardMapper.toDto(saved);
    }

    @Override
    public CardNumberResponse getFullCardNumber(Long id) {
        Card card = cardRepo.findById(id).orElseThrow(() -> new NotFoundException("Карта не найдена"));
        CardNumberResponse responseDTO = new CardNumberResponse();
        responseDTO.setCardNumber(Util.decrypt(card.getEncryptedCardNumber()));
        return responseDTO;
    }

    @Override
    public List<CardDTO> getUserCards(Long userId) {
        return cardRepo.findByUserId(userId).stream()
                .map(CardMapper::toDto)
                .toList();
    }

    @Override
    public void requestBlock(Long cardId, Long userId) {
        Card card = cardRepo.findById(cardId).orElseThrow(() -> new NotFoundException("Карта не найдена"));
        if (!card.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Доступ запрещен");
        }
        card.setStatus(CardStatus.BLOCKED);
        cardRepo.save(card);
    }

    @Override
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Card from = cardRepo.findById(fromId).orElseThrow(() -> new NotFoundException("Карта отправителя не найдена"));
        Card to = cardRepo.findById(toId).orElseThrow(() -> new NotFoundException("Карта получателя не найдена"));
        if (from.getBalance().compareTo(amount) < 0) {
            throw new ValidationException("Недостаточно средств");
        }
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        cardRepo.save(from);
        cardRepo.save(to);
    }

    @Override
    public void blockByAdmin(Long cardId) {
        Card card = cardRepo.findById(cardId).orElseThrow(() -> new NotFoundException("Карта не найдена"));
        card.setStatus(CardStatus.BLOCKED);
        cardRepo.save(card);
    }

    @Override
    public void deleteCard(Long cardId) {
        if (!cardRepo.existsById(cardId)) {
            throw new NotFoundException("Карта не найдена");
        }
        cardRepo.deleteById(cardId);
    }

    @Override
    public List<CardDTO> getAllCards() {
        return cardRepo.findAll().stream()
                .map(CardMapper::toDto)
                .toList();
    }



}
