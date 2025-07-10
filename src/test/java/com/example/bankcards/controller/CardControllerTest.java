package com.example.bankcards.controller;

import com.example.bankcards.config.TestSecurityConfig;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.payload.response.CardNumberResponse;
import com.example.bankcards.payload.request.TransferRequest;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@Import(TestSecurityConfig.class)
@WithMockUser(username = "user1", roles = "USER")
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Principal mockPrincipal;

    @BeforeEach
    void setup() {
        // создаем mock Principal вручную
        mockPrincipal = () -> "user1";
    }

    @Test
    void getMyCards_success() throws Exception {
        User user = User.builder().id(1L).username("user1").build();

        List<CardDTO> cards = List.of(
                new CardDTO(1L, "user1", "**** 1234", new BigDecimal("1000.00"), CardStatus.ACTIVE)
        );

        Mockito.when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        Mockito.when(cardService.getUserCards(1L)).thenReturn(cards);

        mockMvc.perform(get("/api/cards/my").principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].maskedNumber").value("**** 1234"))
                .andExpect(jsonPath("$[0].balance").value(1000.00))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void transfer_success() throws Exception {
        TransferRequest dto = new TransferRequest(1L, 2L, new BigDecimal("50.00"));

        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Деньги успешно переведены!"));

        verify(cardService).transfer(1L, 2L, new BigDecimal("50.00"));
    }

    @Test
    void blockCard_success() throws Exception {
        User user = User.builder().id(1L).username("user1").build();

        Mockito.when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        mockMvc.perform(put("/api/cards/10/block").principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Карта заблокирована"));

        verify(cardService).requestBlock(10L, 1L);
    }

    @Test
    void getFullCardNumber_success() throws Exception {
        CardNumberResponse responseDTO = new CardNumberResponse("1234 5678 9012 3456");

        Mockito.when(cardService.getFullCardNumber(5L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/cards/5/number"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value("1234 5678 9012 3456"));
    }
}
