package com.cadt.sortoutjobbackend.usermanagement.repository;

import com.cadt.sortoutjobbackend.usermanagement.entity.RefreshToken;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUserOrderByCreatedAtAsc(User user);
    
    Optional<RefreshToken> findByUser(User user);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteByUser(User user);
}

