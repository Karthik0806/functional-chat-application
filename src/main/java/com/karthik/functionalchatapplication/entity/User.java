package com.karthik.functionalchatapplication.entity;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
}
