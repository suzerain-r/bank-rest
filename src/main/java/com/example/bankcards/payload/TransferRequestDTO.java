package com.example.bankcards.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDTO {
    Long fromCardId;
    Long toCardId;
    BigDecimal amount;
}
