package com.financemanager.financemanager.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financemanager.financemanager.entities.UserEntity;
import com.financemanager.financemanager.entities.WalletEntity;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
    Optional<WalletEntity> findByUser(UserEntity user);
}
