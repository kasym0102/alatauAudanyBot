package com.alatauBot.utils.reminder;

import com.alatauBot.config.Bot;
import com.alatauBot.utils.DateUtil;
import com.alatauBot.utils.reminder.timerTask.MorningTask;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

@Slf4j
public class Reminder {

    private Bot bot;
    private Timer                  timer = new Timer(true);
    private ArrayList<SendMessage> messagesToMorning;

    public      Reminder(Bot bot) {
        this.bot = bot;
        setMorningTask(17);
    }

    public void setMorningTask(int hour) {
        Date date                    = DateUtil.getHour(hour);
        log.info("Next check db task set to ", date);
        MorningTask checkMorningTask = new MorningTask(bot, this);
        timer.schedule(checkMorningTask, date);
    }
}
