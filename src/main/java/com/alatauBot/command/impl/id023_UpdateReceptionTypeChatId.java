package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.standart.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class id023_UpdateReceptionTypeChatId extends Command {
    private User user;
    private List<User>  users;
    private int i;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        switch (waitingType){
            case START:

        }
        return EXIT;
    }
}
