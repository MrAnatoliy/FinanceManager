package com.financemanager.financemanager.DTOs.internal;

import java.util.List;

import com.financemanager.financemanager.entities.BudgetEntity;
import com.financemanager.financemanager.entities.OperationEntity;

import lombok.Value;

@Value
public class PaymentResult {
    OperationEntity operation;
    List<BudgetEntity> exceededBudgets;
}