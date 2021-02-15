package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.custom.AppealEmployee;
import com.alatauBot.entity.custom.AppealTask;
import com.alatauBot.entity.enums.WaitingType;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;

public class id024_SendComment extends Command {
    private long            employeeChatId;
    private long            receptionTypeChatId;
    private int             taskId;
    private String          nameEmployee;
    private AppealTask appealTask;
    private AppealEmployee appealEmployee;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        switch (waitingType){
            case START:
                if (isButton(68)) {
                    taskId                  = onlyNumbers(update.getCallbackQuery().getMessage().getText().split(next)[1]);
                    appealTask              = appealTaskDao.getIdById(taskId);
                    appealEmployee          = appealEmployeeDao.getOneByTegId(appealTaskDao.getAppealTegIdByTaskId(taskId));
                    employeeChatId          = appealEmployee.getChatId();
                    nameEmployee            = userDao.getFullNameByChatId(appealEmployee.getChatId());
                    receptionTypeChatId     = receptionTypeDao.getChatIdByReceptionTypeId(appealTask.getReceptionTypeId());
                    sendMessage(getText(1021));
                    waitingType             = WaitingType.SET_TEXT;
                }
                return COMEBACK;
            case SET_TEXT:
                deleteMessage(updateMessageId);
                if (hasMessageText()){
                    sendCommentFromUser(employeeChatId);
                    sendCommentFromUser(receptionTypeChatId);
                }
                return EXIT;
        }
        return EXIT;
    }

    private void sendCommentFromUser(long chatId){
        StringBuilder messageFromUser = new StringBuilder();
        messageFromUser.append(getText(1049)).append(taskId).append(next);
        messageFromUser.append(getText(1050)).append(nameEmployee).append(next);
        messageFromUser.append(getText(1052)).append(userDao.getFullNameById(appealTask.getFullName())).append(next);
        messageFromUser.append(getText(1078)).append(updateMessageText);
        try {
            sendMessage(messageFromUser.toString(), chatId);
        } catch (TelegramApiException e) { e.printStackTrace(); }
    }
}
