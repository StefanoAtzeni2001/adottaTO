package com.example.authservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_profile")
@Getter
@Setter
public class UserProfile {

    @Id
    private Long id;  // stessa PK di Auth

    @OneToOne
    @MapsId  // dice a JPA che usa lo stesso valore della PK di Auth
    @JoinColumn(name = "id")
    private Auth auth;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;
}

