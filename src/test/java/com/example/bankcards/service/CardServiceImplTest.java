package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.ValidationException;
import com.example.bankcards.payload.request.CreateCardRequest;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.CardServiceImpl;
import com.example.bankcards.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    @Mock
    private CardRepository cardRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private CardServiceImpl cardService;

    private User mockUser;
    private Card mockCard;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = User.builder()
                .id(1L)
                .username("test")
                .password("pass")
                .build();

        mockCard = Card.builder()
                .id(1L)
                .ownerName("test")
                .encryptedCardNumber(Util.encrypt("1111222233334444"))
                .balance(BigDecimal.valueOf(1000))
                .expiryDate(LocalDate.now().plusYears(2))
                .status(CardStatus.ACTIVE)
                .user(mockUser)
                .build();
    }

    @Test
    void createCard_success() {
        CreateCardRequest dto = new CreateCardRequest(1L, "test");

        when(userRepo.findById(1L)).thenReturn(Optional.of(mockUser));
        when(cardRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CardDTO result = cardService.createCard(dto);

        assertThat(result).isNotNull();
        assertThat(result.getOwnerName()).isEqualTo("test");
        assertThat(result.getStatus()).isEqualTo(CardStatus.ACTIVE);
    }

    @Test
    void createCard_userNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.createCard(new CreateCardRequest(1L, "test")))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void blockByAdmin_success() {
        when(cardRepo.findById(1L)).thenReturn(Optional.of(mockCard));

        cardService.blockByAdmin(1L);

        assertThat(mockCard.getStatus()).isEqualTo(CardStatus.BLOCKED);
        verify(cardRepo).save(mockCard);
    }

    @Test
    void blockByAdmin_cardNotFound() {
        when(cardRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.blockByAdmin(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Карта не найдена");
    }

    @Test
    void transfer_success() {
        Card from = Card.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(1000))
                .user(mockUser)
                .build();

        Card to = Card.builder()
                .id(2L)
                .balance(BigDecimal.valueOf(500))
                .user(mockUser)
                .build();

        when(cardRepo.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepo.findById(2L)).thenReturn(Optional.of(to));

        cardService.transfer(1L, 2L, BigDecimal.valueOf(200));

        assertThat(from.getBalance()).isEqualByComparingTo("800");
        assertThat(to.getBalance()).isEqualByComparingTo("700");

        verify(cardRepo).save(from);
        verify(cardRepo).save(to);
    }

    @Test
    void transfer_insufficientFunds() {
        Card from = Card.builder().id(1L).balance(BigDecimal.valueOf(100)).user(mockUser).build();
        Card to = Card.builder().id(2L).balance(BigDecimal.valueOf(500)).user(mockUser).build();

        when(cardRepo.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepo.findById(2L)).thenReturn(Optional.of(to));

        assertThatThrownBy(() -> cardService.transfer(1L, 2L, BigDecimal.valueOf(200)))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Недостаточно средств");
    }

    @Test
    void deleteCard_success() {
        when(cardRepo.existsById(1L)).thenReturn(true);

        cardService.deleteCard(1L);

        verify(cardRepo).deleteById(1L);
    }

    @Test
    void deleteCard_notFound() {
        when(cardRepo.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> cardService.deleteCard(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Карта не найдена");
    }

    @Test
    void getAllCards_returnsMappedDTOs() {
        when(cardRepo.findAll()).thenReturn(List.of(mockCard));

        List<CardDTO> result = cardService.getAllCards();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOwnerName()).isEqualTo("test");
    }
}
