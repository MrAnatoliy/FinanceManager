package com.financemanager.financemanager.exceptions;

public class UserAlreadyExistsException extends BuisnessException{
    public UserAlreadyExistsException(String username){
        super("User with username " + username + " already exists");
    }
}
