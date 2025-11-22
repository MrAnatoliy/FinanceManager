package com.financemanager.financemanager.DTOs.responses;

import com.financemanager.financemanager.entities.WalletEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WalletResponseDto {
    private Integer balance;

    public WalletResponseDto(WalletEntity walletEntity) {
        this.balance = walletEntity.getBalance();
    }
}
