package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.Util;

public class CardMapper {
    public static CardDTO toDto(Card card) {
        CardDTO dto = new CardDTO();
        dto.setId(card.getId());
        dto.setUsername(card.getUsername());
        dto.setMaskedNumber(Util.getMaskedNumber(card.getEncryptedCardNumber()));
        dto.setBalance(card.getBalance());
        dto.setStatus(card.getStatus());
        return dto;
    }

}
