package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.custom.ReminderTask;
import com.alatauBot.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class id014_ReminderDelete extends Command {

    private List<ReminderTask> reminderTaskList;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                reminderTaskList = factory.getReminderTaskDao().getAll();
                return false;
        }
        return EXIT;
    }
}
