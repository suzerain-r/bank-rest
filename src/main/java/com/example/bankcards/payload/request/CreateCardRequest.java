package com.example.bankcards.payload.request;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateCardRequest {
    Long userId;
    String ownerName;
}
