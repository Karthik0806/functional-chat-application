package com.karthik.functionalchatapplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(indexes = {
                @Index(name = "idx_refresh_username", columnList = "username"),
                @Index(name = "idx_refresh_expiry", columnList = "expiryDate")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String token;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private Instant expiryDate;
}
