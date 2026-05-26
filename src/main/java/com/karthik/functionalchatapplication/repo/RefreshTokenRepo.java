package com.karthik.functionalchatapplication.repo;

import com.karthik.functionalchatapplication.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);

    @Transactional
    @Modifying
    void deleteByUsername(String username);
}
