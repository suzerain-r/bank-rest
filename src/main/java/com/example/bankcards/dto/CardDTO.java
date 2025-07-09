package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {
    Long id;
    String ownerName;
    String maskedNumber;
    BigDecimal balance;
    CardStatus status;
}
