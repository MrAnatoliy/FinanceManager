package com.financemanager.financemanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financemanager.financemanager.entities.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    
}
