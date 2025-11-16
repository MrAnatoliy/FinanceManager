package com.financemanager.financemanager.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financemanager.financemanager.entities.CategoryEntity;
import com.financemanager.financemanager.entities.OperationEntity;
import com.financemanager.financemanager.entities.UserEntity;
import com.financemanager.financemanager.entities.WalletEntity;
import com.financemanager.financemanager.enums.OperationType;
import com.financemanager.financemanager.exceptions.NotEnoughFundsException;
import com.financemanager.financemanager.repositories.OperationRepository;
import com.financemanager.financemanager.repositories.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final OperationRepository operationRepository;

    public WalletEntity getWalletByUser(UserEntity user) {
        return walletRepository.findByUser(user).orElse(null);
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
        new_operation.setOperation_type(OperationType.REPLENISHMENT);
        new_operation.setOperation_value(amount);
        new_operation.setWallet(wallet);

        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);

        return operationRepository.save(new_operation);
    }

    @Transactional
    public OperationEntity withdraw(int amount, WalletEntity wallet) throws NotEnoughFundsException {
        OperationEntity new_operation = new OperationEntity();
        new_operation.setOperation_type(OperationType.REPLENISHMENT);
        new_operation.setOperation_value(amount);
        new_operation.setWallet(wallet);

        try_withdraw_funds(amount, wallet, new_operation);

        return operationRepository.save(new_operation);
    }

    @Transactional
    public OperationEntity execute_payment(int amount, CategoryEntity category, WalletEntity wallet) throws NotEnoughFundsException {
        OperationEntity new_operation = new OperationEntity();
        new_operation.setOperation_type(OperationType.PAYMENT);
        new_operation.setOperation_category(category);
        new_operation.setOperation_value(amount);
        new_operation.setWallet(wallet);

        try_withdraw_funds(amount, wallet, new_operation);

        return operationRepository.save(new_operation);
    }

    @Transactional
    public OperationEntity execute_transaction(int amount, WalletEntity from_wallet, WalletEntity to_wallet) throws NotEnoughFundsException {
        OperationEntity withdraw_transfer_opeation = new OperationEntity();
        withdraw_transfer_opeation.setOperation_type(OperationType.TRANSFER);
        withdraw_transfer_opeation.setOperation_value(-amount);
        withdraw_transfer_opeation.setWallet(from_wallet);
        
        try_withdraw_funds(amount, from_wallet, withdraw_transfer_opeation);
        
        OperationEntity top_up_transfer_opeation = new OperationEntity();
        withdraw_transfer_opeation.setOperation_type(OperationType.TRANSFER);
        withdraw_transfer_opeation.setOperation_value(amount);
        withdraw_transfer_opeation.setWallet(to_wallet);
        
        
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
            throw new NotEnoughFundsException(wallet.getBalance() - amount, operation);
        }
    }


}
