package com.cadt.sortoutjobbackend.usermanagement.repository;

import com.cadt.sortoutjobbackend.usermanagement.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByPhoneAndVerifiedFalse(String phone);

    @Modifying
    void deleteByPhone(String phone);
}
