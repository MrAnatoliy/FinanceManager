package com.financemanager.financemanager.exceptions;

public class CategoryNotFoundException extends BuisnessException{
    public CategoryNotFoundException(String categoty_name) {
        super("Category with name " + categoty_name + " was not found");
    }
}
