package com.cadt.sortoutjobbackend.usermanagement.repository;

import com.cadt.sortoutjobbackend.usermanagement.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    
    @Modifying
    void deleteByUserId(Long userId);
}
