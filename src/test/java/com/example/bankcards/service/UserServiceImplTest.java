package com.example.bankcards.service;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.URole;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = User.builder()
                .id(1L)
                .username("john")
                .password("pass")
                .role(URole.USER)
                .build();
    }

    @Test
    void findByUsername_ShouldReturnUserDTO_WhenUserExists() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));

        UserDTO dto = userService.findByUsername("john");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUsername()).isEqualTo("john");
        assertThat(dto.getRole()).isEqualTo(URole.USER);
    }

    @Test
    void findByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUsername("john"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void getCurrentUser_ShouldReturnUserDTO_FromSecurityContext() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("john", null, List.of());
        SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));

        UserDTO dto = userService.getCurrentUser();

        assertThat(dto.getUsername()).isEqualTo("john");
    }


    @Test
    void getAllUsers_ShouldReturnListOfDTOs() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserDTO> users = userService.getAllUsers();

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getUsername()).isEqualTo("john");
    }

    @Test
    void deleteUser_ShouldCallDelete_WhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }
}
