package com.alatauBot.utils.reminder.timerTask;

import com.alatauBot.config.Bot;
import com.alatauBot.utils.DateUtil;
import com.alatauBot.utils.reminder.Reminder;
import com.alatauBot.entity.custom.ReminderTask;
import com.alatauBot.entity.standart.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.List;

public class MorningTask extends AbstractTask {

    private List<User>          users;
    private List<ReminderTask>  tasks;
    private Date                start;

    public          MorningTask(Bot bot, Reminder reminder) {
        super(bot, reminder);
    }

    @Override
    public void     run() {
        users           = userDao.getAll();
        start           = new Date();
        Date dateEnd    = new Date();
        dateEnd.setDate(dateEnd.getDay() + 1);
        tasks           = reminderTaskDao.getByTime(start, dateEnd);
        checkMessage();
        reminder.setMorningTask(17);
    }

    private void    checkMessage() {
        if (tasks != null && tasks.size() != 0) users.forEach(this::sendMessage);
    }

    private void    sendMessage(User user) {
        tasks.forEach((e) -> {
            if (DateUtil.getDayDate(e.getDateBegin()).equals(DateUtil.getDayDate(start))) {
                try {
                    bot.execute(new SendMessage().setChatId(user.getChatId()).setText(e.getText()));
                } catch (TelegramApiException ex) {}
            }
        });
    }
}
