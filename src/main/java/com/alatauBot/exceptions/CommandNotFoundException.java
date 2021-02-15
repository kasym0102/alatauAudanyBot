package com.alatauBot.exceptions;

public class CommandNotFoundException extends Exception {
    public CommandNotFoundException(Exception e) {
        super(e);
    }
}
