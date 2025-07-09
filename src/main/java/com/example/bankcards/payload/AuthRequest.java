package com.example.bankcards.payload;

import lombok.Data;

@Data
public class AuthRequest {
    String username;
    String password;
}
