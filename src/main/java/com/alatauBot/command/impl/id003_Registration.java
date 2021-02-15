package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.entity.standart.User;
import com.alatauBot.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class id003_Registration extends Command {

    private User user;
    private int uptMess;

    @Override
    public boolean execute() throws TelegramApiException {
        switch (waitingType) {
            case START:
                if (isRegistered()) {
                    sendMessageWithAddition();
                    return EXIT;
                }
                deleteAll(uptMess);
                user = new User();
                user.setChatId(chatId);
                getName();
                waitingType = WaitingType.SET_FULL_NAME;
                return COMEBACK;
            case SET_FULL_NAME:
                deleteAll(uptMess);
                if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().length() <= 50) {
                    user.setFullName(update.getMessage().getText());
                    getPhone();
                    waitingType = WaitingType.SET_PHONE_NUMBER;
                } else {
                    getName();
                }
                return COMEBACK;
            case SET_PHONE_NUMBER:
                deleteAll(uptMess);
                if (hasContact()) {
                    user.setPhone(update.getMessage().getContact().getPhoneNumber());
                    user.setUserName(update.getMessage().getFrom().getUserName());
                    userDao.insert(user);
                    sendMessageWithAddition();
                    return EXIT;
                } else {
                    getPhone();
                }
                return COMEBACK;
        }
        return COMEBACK;
    }

    private void getName() throws TelegramApiException { uptMess = sendMessage(Const.SET_FULL_NAME_MESSAGE); }

    private void getPhone() throws TelegramApiException { uptMess = sendMessage(Const.SEND_CONTACT_MESSAGE); }
}
