package com.financemanager.financemanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financemanager.financemanager.entities.OperationEntity;

public interface OperationRepository extends JpaRepository<OperationEntity, Long>{
    
}
