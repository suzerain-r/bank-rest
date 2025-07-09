package com.example.bankcards.service;

import com.example.bankcards.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO findByUsername(String username);
    UserDTO getCurrentUser();
    List<UserDTO> getAllUsers();
    void deleteUser(Long userId);
}
