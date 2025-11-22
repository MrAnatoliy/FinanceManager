package com.financemanager.financemanager.exceptions;

public class UserNotFoundException extends BuisnessException{
    public UserNotFoundException(String username){
        super("User with username " + username + " was not found");
    }
}
