package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.CardStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardDTO {
    Long id;
    String ownerName;
    String encryptedCardNumber;
    BigDecimal balance;
    CardStatus status;
}
