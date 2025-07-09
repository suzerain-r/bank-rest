package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.payload.CreateCardRequestDTO;

import java.math.BigDecimal;
import java.util.List;

public interface CardService {
    List<CardDTO> getUserCards(Long userId);
    void requestBlock(Long cardId, Long userId);
    void transfer(Long fromId, Long toId, BigDecimal amount);


    CardDTO createCard(CreateCardRequestDTO createCardRequestDTO);
    void blockByAdmin(Long cardId);
    void deleteCard(Long cardId);
    List<CardDTO> getAllCards();
}
