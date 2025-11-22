package com.financemanager.financemanager.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "budget")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BudgetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long operationId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "walletId")
    private WalletEntity wallet;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private CategoryEntity budgetCategory;

    private int budgetLimit;
    private int currentBudgetAmount;
}
