package com.example.bankcards.payload;


import lombok.Data;

@Data
public class CreateCardRequestDTO {
    Long userId;
    String ownerName;
}
