package com.example.bankcards.dto;


import com.example.bankcards.entity.enums.URole;
import lombok.Data;

@Data
public class UserDTO {

    Long id;
    private String username;
    private URole role;
}
