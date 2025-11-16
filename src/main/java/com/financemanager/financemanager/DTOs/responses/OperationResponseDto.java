package com.financemanager.financemanager.DTOs.responses;

import com.financemanager.financemanager.entities.OperationEntity;
import com.financemanager.financemanager.enums.OperationType;

import lombok.Data;

@Data
public class OperationResponseDto {

    private String wallet_owner_username;
    private int opeation_value;
    private OperationType operation_type;
    private String operation_category;

    public OperationResponseDto(OperationEntity operation) {
        this.wallet_owner_username = operation.getWallet().getUser().getUsername();
        this.opeation_value = operation.getOperation_value();
        this.operation_type = operation.getOperation_type();
        if(operation.getOperation_category() != null){
            this.operation_category = operation.getOperation_category().getCategoryName();
        }
    }
}
