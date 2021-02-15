package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.custom.CitizensRegistration;
import com.alatauBot.entity.custom.ReceptionEmployee;
import com.alatauBot.entity.custom.SendMember;
import com.alatauBot.entity.standart.User;
import com.alatauBot.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class id021_CitizensMember extends Command {

    private List<CitizensRegistration>  registration;
    private ReceptionEmployee receptionEmployee;
    private List<ReceptionEmployee>     receptionEmployees;
    private int                         i = 0;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        if (!isReceptionEmployee()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                receptionEmployee       = receptionEmployeeDao.getAllByChatId(chatId).get(0);
                if (factory.getCitizensRegistrationDao().getByReceptionId(receptionEmployee.getReceptionId()) != null) {
                    registration = citizensRegistrationDao.getByReceptionId(receptionEmployee.getReceptionId());
                    registration.forEach(e -> {
                        List<User> users                           = userDao.getAllUserByChatId(e.getChatId());
                        try {
                            String result                       = String.format(getText(1070), userDao.getFullNameByChatId(e.getChatId()), e.getId(), receptionTypeDao.getById(e.getReceptionId()).getComment());
                            sendMessageWithKeyboard(result,keyboardMarkUpDao.select(16), e.getChatId());
                        } catch (TelegramApiException telegramApiException) { telegramApiException.printStackTrace(); }
                        SendMember sendMember               = new SendMember();
                        sendMember.setChatId(registration.get(i).getChatId());
                        sendMember.setReceptionId(e.getReceptionId());
                        sendMember.setRegistrationId(e.getId());
                        factory.getSendMemberDao().insert(sendMember); i++;
                    });
                    toDeleteKeyboard(sendMessage("Напоминание отправлено"));
                } else {
                    sendMessage("Зарегистрированых пользователей на прием нету");
                }
                return EXIT;
        }
        return EXIT;
    }
}
