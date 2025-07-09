package com.example.bankcards.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransferRequest {
    Long fromCardId;
    Long toCardId;
    BigDecimal amount;
}
