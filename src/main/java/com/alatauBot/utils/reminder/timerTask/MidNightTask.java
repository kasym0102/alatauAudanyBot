package com.alatauBot.utils.reminder.timerTask;

import com.alatauBot.config.Bot;
import com.alatauBot.utils.reminder.Reminder;
import com.alatauBot.entity.custom.AppealTask;

import java.util.Date;
import java.util.List;

public class MidNightTask extends AbstractTask {

    private List<AppealTask>  tasks;
    private Date                deadline;

    public MidNightTask(Bot bot, Reminder reminder) { super(bot, reminder); }

    @Override
    public void run() {
       tasks    =  taskDao.getAll();
       deadline = new Date();
        for (AppealTask task : tasks) {

        }
    }
}
