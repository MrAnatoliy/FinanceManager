package com.financemanager.financemanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

import com.financemanager.financemanager.entities.OperationEntity;
import com.financemanager.financemanager.entities.WalletEntity;

public interface OperationRepository extends JpaRepository<OperationEntity, Long>{
    List<OperationEntity> findByWalletAndCreatedAtBetweenOrderByCreatedAtDesc(
        WalletEntity wallet,
        Instant from,
        Instant to
    );
}
