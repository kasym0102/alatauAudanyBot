package com.alatauBot.utils.reminder.timerTask;

import com.alatauBot.config.Bot;
import com.alatauBot.dao.DaoFactory;
import com.alatauBot.dao.impl.AppealTaskDao;
import com.alatauBot.dao.impl.ReminderTaskDao;
import com.alatauBot.dao.impl.UserDao;
import com.alatauBot.utils.reminder.Reminder;

import java.util.TimerTask;

public abstract class AbstractTask extends TimerTask {

    protected Bot bot;
    protected Reminder reminder;
    protected DaoFactory factory         = DaoFactory.getInstance();
    protected ReminderTaskDao reminderTaskDao = factory.getReminderTaskDao();
    protected UserDao userDao         = factory.getUserDao();
    protected AppealTaskDao taskDao         = factory.getAppealTaskDao();

    public AbstractTask(Bot bot, Reminder reminder) {
        this.bot        = bot;
        this.reminder   = reminder;
    }
}
