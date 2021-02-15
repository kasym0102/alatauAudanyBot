package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;

public class id016_ShowInfoFile extends Command {
    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        switch (waitingType) {
            case START:
                if (update.getMessage().hasText()) {
                    if (factory.getMessageDao().getMessage(46).getFile() == null) {
                        sendMessage(1025);
                        return EXIT;
                    } else if (factory.getMessageDao().getMessage(47).getFile() == null) {
                        sendMessage(1025);
                        return EXIT;
                    }
                }
                return EXIT;
        }
        return EXIT;
    }
}
