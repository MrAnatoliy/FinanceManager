package com.financemanager.financemanager.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financemanager.financemanager.entities.BudgetEntity;
import com.financemanager.financemanager.entities.CategoryEntity;
import com.financemanager.financemanager.entities.WalletEntity;

public interface BudgetRepository extends JpaRepository<BudgetEntity, Long>{
    List<BudgetEntity> findByWallet(WalletEntity wallet);

    List<BudgetEntity> findAllByWalletAndBudgetCategory(WalletEntity wallet, CategoryEntity category);
}
