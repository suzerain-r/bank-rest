package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.payload.request.CreateCardRequest;
import com.example.bankcards.payload.response.CardNumberResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CardService {
    List<CardDTO> getUserCards(Long userId);
    void requestBlock(Long cardId, Long userId);
    void transfer(Long fromId, Long toId, BigDecimal amount);
    CardNumberResponse getFullCardNumber(Long userId);

    CardDTO createCard(CreateCardRequest createCardRequest);
    void blockByAdmin(Long cardId);
    void deleteCard(Long cardId);
    List<CardDTO> getAllCards();
}
