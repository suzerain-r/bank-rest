package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.URole;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private URole role;

    @OneToMany(mappedBy = "user")
    private List<Card> cards;

}
