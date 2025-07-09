package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;

public class CardMapper {
    public static CardDTO toDto(Card card) {
        CardDTO dto = new CardDTO();
        dto.setId(card.getId());
        dto.setOwnerName(card.getOwnerName());
        dto.setEncryptedCardNumber(card.getEncryptedCardNumber());
        dto.setBalance(card.getBalance());
        dto.setStatus(card.getStatus());
        return dto;
    }

}
