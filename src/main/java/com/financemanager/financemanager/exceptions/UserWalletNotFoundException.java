package com.financemanager.financemanager.exceptions;

public class UserWalletNotFoundException extends BuisnessException{
    public UserWalletNotFoundException(String username){
        super("Wallet of user with username " + username + " was not found");
    }
}
