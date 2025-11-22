package com.financemanager.financemanager.DTOs.responses;

import com.financemanager.financemanager.entities.BudgetEntity;

import lombok.Data;

@Data
public class BudgetResponseDto {
    private String categoryName;
    private int budgetLimit;
    private int currentBudgetAmount;

    public BudgetResponseDto(BudgetEntity budgetEntity) {
        this.categoryName = budgetEntity.getBudgetCategory().getCategoryName();
        this.budgetLimit = budgetEntity.getBudgetLimit();
        this.currentBudgetAmount = budgetEntity.getCurrentBudgetAmount();
    }
}
