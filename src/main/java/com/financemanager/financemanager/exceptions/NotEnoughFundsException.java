package com.financemanager.financemanager.exceptions;

import com.financemanager.financemanager.entities.OperationEntity;

public class NotEnoughFundsException extends BuisnessException{
    public NotEnoughFundsException(int missing, OperationEntity operation){
        super("Not enough funds (" + missing + ") for " + operation.getOperation_type().toString().toLowerCase() + " operation");
    }
}
