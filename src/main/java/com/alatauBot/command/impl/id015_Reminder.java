package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.custom.ReminderTask;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.utils.Const;
import com.alatauBot.utils.DateKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.List;

public class id015_Reminder extends Command {

    private ReminderTask reminderTask;
    private DateKeyboard dateKeyboard;
    private Date                dateStart;
    private List<ReminderTask>  reminderTaskList;
    private int                 deleteMessageId;
    private boolean             isUpdate = false;
    private int                 reminderTaskId;

    @Override
    public boolean  execute()           throws TelegramApiException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                sendListReminder();
                waitingType = WaitingType.CHOICE_OPTION;
                return COMEBACK;
            case CHOICE_OPTION:
                deleteMessage(deleteMessageId);
                deleteMessage(updateMessageId);
                if (hasMessageText()) {
                    if (isCommand("/new")) {
                        dateKeyboard    = new DateKeyboard();
                        sendStartDate();
                        waitingType     = WaitingType.START_DATE;
                    } else if (isCommand("/del")) {
                        reminderTaskId  = reminderTaskList.get(Integer.parseInt(updateMessageText.replaceAll("[^0-9]", ""))).getId();
                        factory.getReminderTaskDao().delete(reminderTaskId);
                        sendListReminder();
                        waitingType     = WaitingType.CHOICE_OPTION;
                    } else if (isCommand("/st")) {
                        reminderTaskId  = reminderTaskList.get(Integer.parseInt(updateMessageText.replaceAll("[^0-9]", ""))).getId();
                        dateKeyboard    = new DateKeyboard();
                        sendStartDate();
                        isUpdate        = true;
                        waitingType     = WaitingType.START_DATE;
                    }
                }
                return COMEBACK;
            case START_DATE:
                deleteMessage(updateMessageId);
                if (hasCallbackQuery()) {
                    if (dateKeyboard.isNext(updateMessageText)) {
                        sendStartDate();
                        return COMEBACK;
                    }
                    dateStart = dateKeyboard.getDateDate(updateMessageText);
                    dateStart.setHours(0);
                    dateStart.setMinutes(0);
                    dateStart.setSeconds(0);
                    if (isUpdate) {
                        reminderTask    = factory.getReminderTaskDao().getById(reminderTaskId);
                    } else {
                        reminderTask    = new ReminderTask();
                    }
                    reminderTask.setDateBegin(dateStart);
                    sendMessage("Введите текст сообщения");
                    waitingType         = WaitingType.SET_TEXT;
                }
                return COMEBACK;
            case SET_TEXT:
                deleteMessage(updateMessageId);
                if (hasMessageText()) {
                    reminderTask.setText(updateMessageText);
                    if (isUpdate) {
                        factory.getReminderTaskDao().update(reminderTask);
                    } else { factory.getReminderTaskDao().insert(reminderTask); }
                    sendListReminder();
                    waitingType = WaitingType.CHOICE_OPTION;
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private void    sendListReminder()  throws TelegramApiException {
        String formatMessage        = getText(44); //46
        StringBuilder stringBuilder = new StringBuilder();
        reminderTaskList            = factory.getReminderTaskDao().getAll();
        String format               = getText(45); //47
        for (int i = 0; i < reminderTaskList.size(); i++) {
            ReminderTask reminderTask = reminderTaskList.get(i);
            stringBuilder.append(String.format(format, "/del" + i, "/st" + i, reminderTask.getText())).append(next);
        }
        deleteMessageId             = sendMessage(String.format(formatMessage, stringBuilder.toString(), "/new"));
    }

    private int     sendStartDate()     throws TelegramApiException {
        return toDeleteKeyboard(sendMessageWithKeyboard("Выберите начальную дату, для подробного отчета", dateKeyboard.getCalendarKeyboard()));
    }

    private boolean isCommand(String command) {
        return updateMessageText.startsWith(command);
    }
}
