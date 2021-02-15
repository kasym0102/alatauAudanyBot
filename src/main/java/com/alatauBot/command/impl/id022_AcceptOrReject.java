package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.custom.CitizensRegistration;
import com.alatauBot.entity.custom.SendMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class id022_AcceptOrReject extends Command {
    private int i = 0, id;
    private CitizensRegistration registration;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        deleteMessage(updateMessageId);
        List<SendMember> sendMembers               = sendMemberDao.getAllByChatId(chatId);
        switch (waitingType){
            case START:
                if (isButton(66)){
                    deleteMessage(updateMessageId);
                    registration = new CitizensRegistration();
                    registration.setStatus("Подтвержден");
                    registration.setId(Integer.parseInt(update.getCallbackQuery().getMessage().getText().split("№")[1].replaceAll("[^0-9]", " ").split(" ")[0]));
                }
                if (isButton(67)){
                    registration = new CitizensRegistration();
                    deleteMessage(updateMessageId);
                    registration.setStatus("Не подтвержден");
                    registration.setId(Integer.parseInt(update.getCallbackQuery().getMessage().getText().split("№")[1].replaceAll("[^0-9]", " ").split(" ")[0]));
                }
                sendMessage(1075);
                citizensRegistrationDao.update(registration);
        }
        return EXIT;
    }
}