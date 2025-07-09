package com.example.bankcards.dto;


import com.example.bankcards.entity.enums.URole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    Long id;
    private String username;
    private URole role;
}
