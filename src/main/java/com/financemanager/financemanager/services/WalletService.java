package com.financemanager.financemanager.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financemanager.financemanager.DTOs.internal.PaymentResult;
import com.financemanager.financemanager.entities.BudgetEntity;
import com.financemanager.financemanager.entities.CategoryEntity;
import com.financemanager.financemanager.entities.OperationEntity;
import com.financemanager.financemanager.entities.UserEntity;
import com.financemanager.financemanager.entities.WalletEntity;
import com.financemanager.financemanager.enums.OperationType;
import com.financemanager.financemanager.exceptions.NotEnoughFundsException;
import com.financemanager.financemanager.exceptions.UserWalletNotFoundException;
import com.financemanager.financemanager.repositories.BudgetRepository;
import com.financemanager.financemanager.repositories.OperationRepository;
import com.financemanager.financemanager.repositories.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletService {

    //private final static Logger logger = Logger.getLogger(WalletService.class.getName());

    private final WalletRepository walletRepository;
    private final OperationRepository operationRepository;
    private final BudgetRepository budgetRepository;

    public WalletEntity getWalletByUser(UserEntity user) {
        return walletRepository.findByUser(user).orElseThrow(
            () -> new UserWalletNotFoundException(user.getUsername())
        );   // возвращаем Optional
    }

    @Transactional
    public WalletEntity create(UserEntity user){
        WalletEntity new_wallet = new WalletEntity();
        new_wallet.setUser(user);
        new_wallet.setBalance(0);

        return walletRepository.save(new_wallet);
    }

    @Transactional
    public OperationEntity top_up(int amount, WalletEntity wallet) {
        OperationEntity new_operation = new OperationEntity();
        new_operation.setCreatedAt(Instant.now());
        new_operation.setOperationType(OperationType.REPLENISHMENT);
        new_operation.setOperationValue(amount);
        new_operation.setWallet(wallet);

        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);

        return operationRepository.save(new_operation);
    }

    @Transactional
    public OperationEntity withdraw(int amount, WalletEntity wallet) throws NotEnoughFundsException {
        OperationEntity new_operation = new OperationEntity();
        new_operation.setCreatedAt(Instant.now());
        new_operation.setOperationType(OperationType.WITHDRAWAL);
        new_operation.setOperationValue(-amount);
        new_operation.setWallet(wallet);

        try_withdraw_funds(amount, wallet, new_operation);

        return operationRepository.save(new_operation);
    }

    @Transactional
    public PaymentResult execute_payment(int amount, CategoryEntity category, WalletEntity wallet) throws NotEnoughFundsException {
        OperationEntity new_operation = new OperationEntity();
        new_operation.setCreatedAt(Instant.now());
        new_operation.setOperationType(OperationType.PAYMENT);
        new_operation.setOperationCategory(category);
        new_operation.setOperationValue(-amount);
        new_operation.setWallet(wallet);

        List<BudgetEntity> category_budget_list;
        List<BudgetEntity> budgets_with_excessed_limits = new ArrayList<>();
        category_budget_list = budgetRepository.findAllByWalletAndBudgetCategory(wallet, category);

        if (!category_budget_list.isEmpty()) {
            for(BudgetEntity budget : category_budget_list){
                int new_budget_amount = budget.getCurrentBudgetAmount() + amount;
                if(new_budget_amount > budget.getBudgetLimit()){
                    budgets_with_excessed_limits.add(budget);
                }
                budget.setCurrentBudgetAmount(new_budget_amount);
            }
        }

        try_withdraw_funds(amount, wallet, new_operation);

        OperationEntity operation = operationRepository.save(new_operation);
        return new PaymentResult(operation, budgets_with_excessed_limits);
    }

    @Transactional
    public OperationEntity execute_transaction(int amount, WalletEntity from_wallet, WalletEntity to_wallet) throws NotEnoughFundsException {
        OperationEntity withdraw_transfer_opeation = new OperationEntity();
        withdraw_transfer_opeation.setCreatedAt(Instant.now());
        withdraw_transfer_opeation.setOperationType(OperationType.TRANSFER);
        withdraw_transfer_opeation.setOperationValue(-amount);
        withdraw_transfer_opeation.setWallet(from_wallet);
        
        try_withdraw_funds(amount, from_wallet, withdraw_transfer_opeation);
        
        OperationEntity top_up_transfer_opeation = new OperationEntity();
        top_up_transfer_opeation.setCreatedAt(Instant.now());
        top_up_transfer_opeation.setOperationType(OperationType.TRANSFER);
        top_up_transfer_opeation.setOperationValue(amount);
        top_up_transfer_opeation.setWallet(to_wallet);
        
        
        to_wallet.setBalance(to_wallet.getBalance() + amount);
        walletRepository.save(to_wallet);
        
        operationRepository.save(top_up_transfer_opeation);
        return operationRepository.save(withdraw_transfer_opeation);
    }

    private void try_withdraw_funds(int amount, WalletEntity wallet, OperationEntity operation) throws NotEnoughFundsException {
        if(wallet.getBalance() - amount >= 0){
            wallet.setBalance(wallet.getBalance() - amount);
            walletRepository.save(wallet);
        } else {
            throw new NotEnoughFundsException(Math.abs(wallet.getBalance() - amount), operation);
        }
    }


}
