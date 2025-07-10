package com.example.bankcards.controller;

import com.example.bankcards.config.TestSecurityConfig;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.URole;
import com.example.bankcards.payload.request.CreateCardRequest;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(TestSecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCard() throws Exception {
        CreateCardRequest request = new CreateCardRequest(1L);
        CardDTO response = new CardDTO(
                1L,
                "John Doe",
                "****-****-****-5432",
                new BigDecimal("1000.00"),
                CardStatus.ACTIVE
        );

        when(cardService.createCard(request)).thenReturn(response);

        mockMvc.perform(post("/api/admin/cards/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.maskedNumber").value(response.getMaskedNumber()))
                .andExpect(jsonPath("$.balance").value(response.getBalance().doubleValue()))
                .andExpect(jsonPath("$.status").value(response.getStatus().toString()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void blockCard() throws Exception {
        Long cardId = 1L;
        doNothing().when(cardService).blockByAdmin(cardId);

        mockMvc.perform(put("/api/admin/cards/{id}/block", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Карта заблокирована"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCard() throws Exception {
        Long cardId = 1L;
        doNothing().when(cardService).deleteCard(cardId);

        mockMvc.perform(delete("/api/admin/cards/{id}", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Карта была удалена"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUser() throws Exception {
        Long userId = 2L;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/admin/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Пользователь был удален"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllCards() throws Exception {
        List<CardDTO> cards = List.of(
                new CardDTO(1L, "User 1", "****-****-****-1234", BigDecimal.valueOf(500), CardStatus.ACTIVE)
        );

        when(cardService.getAllCards()).thenReturn(cards);

        mockMvc.perform(get("/api/admin/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(cards.size()))
                .andExpect(jsonPath("$[0].id").value(cards.get(0).getId()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllUsers() throws Exception {
        List<UserDTO> users = List.of(
                new UserDTO(1L, "admin", URole.ADMIN),
                new UserDTO(2L, "john", URole.USER)
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(users.size()))
                .andExpect(jsonPath("$[0].id").value(users.get(0).getId()))
                .andExpect(jsonPath("$[0].username").value(users.get(0).getUsername()))
                .andExpect(jsonPath("$[0].role").value(users.get(0).getRole().toString()))
                .andExpect(jsonPath("$[1].id").value(users.get(1).getId()))
                .andExpect(jsonPath("$[1].username").value(users.get(1).getUsername()))
                .andExpect(jsonPath("$[1].role").value(users.get(1).getRole().toString()));
    }
}
