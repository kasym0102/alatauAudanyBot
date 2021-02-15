package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;

public class id017_MapLocationSend extends Command {
    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        bot.execute(new SendLocation().setLatitude(Float.parseFloat(propertiesDao.getPropertiesLatitude(6))).setLongitude(Float.parseFloat(propertiesDao.getPropertiesLongitude(6))).setChatId(chatId));
        return EXIT;
    }
}
