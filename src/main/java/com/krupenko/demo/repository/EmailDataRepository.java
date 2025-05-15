package com.krupenko.demo.repository;

import com.krupenko.demo.entity.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

    Optional<EmailData> findByEmail(String email);

    int countByUserId(Long userId);

}
