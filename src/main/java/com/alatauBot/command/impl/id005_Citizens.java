package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.utils.ButtonsLeaf;
import com.alatauBot.entity.custom.CitizensInfo;
import com.alatauBot.entity.custom.CitizensRegistration;
import com.alatauBot.entity.custom.ReceptionEmployee;
import com.alatauBot.entity.custom.ReceptionType;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
public class id005_Citizens extends Command {
    private List<String> list, list2;
    private List<ReceptionType> receptions;
    private CitizensInfo citizensInfo;
    private ButtonsLeaf buttonsLeaf;
    private String dedline;
    private List<ReceptionEmployee> receptionEmployees;
    private SimpleDateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd");
    private CitizensRegistration citizensRegistration;
    private ReceptionType receptionType;
    private int uptMess;

    @Override
    public boolean execute() throws TelegramApiException {
        deleteMessage(updateMessageId);
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                citizensInfo = citizensInfoDao.getById(5);
                if (citizensInfo.getDocument() != null) {
                    bot.execute(new SendDocument().setDocument(citizensInfo.getDocument()).setChatId(chatId));
                } else {
                    sendMessage(1025);
                }
                list = new ArrayList<>();
                receptions = Optional.ofNullable(receptionTypeDao.getAll(1)).map(rec1 -> {
                    rec1.forEach(rec -> list.add(rec.getName()));
                    buttonsLeaf = new ButtonsLeaf(list);
                    return rec1;
                }).orElse(new ArrayList<>());
                sendMessWKey(8, buttonsLeaf);
                waitingType = WaitingType.CHOOSE_RECEPTION;
                return COMEBACK;
            case CHOOSE_RECEPTION:
                deleteAll(uptMess);
                if (hasCallbackQuery()) {
                    receptionType = receptions.get(Integer.parseInt(updateMessageText));
                    citizensRegistration = new CitizensRegistration();
                    citizensRegistration.setChatId(chatId);
                    citizensRegistration.setReceptionId(receptions.get(Integer.parseInt(updateMessageText)).getId());
                    citizensRegistration.setStatus("Записан");
                    citizensRegistration.setCitizensDate(dateFormat.format(new Date()).split("-")[0] + "-" + dateFormat.format(new Date()).split("-")[1] + "-" + "пятница");
                    citizensRegistration.setCitizensTime(factory.getCitizensInfoDao().getById(receptionType.getId()).getTime());
                    sendMess(1004);
                    waitingType = WaitingType.SET_IIN;
                }
                return COMEBACK;
            case SET_IIN:
                deleteAll(uptMess);
                try {
                    Long.parseLong(update.getMessage().getText());
                } catch (NumberFormatException e) {
                    sendMess(1005);
                    sendMess(1004);
                    return COMEBACK;
                }
                if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().length() == 12) {
                    citizensRegistration.setIin(updateMessageText);
                    sendMess(1003);
                    waitingType = WaitingType.SET_QUESTION;
                } else {
                    sendMess(1004);
                }
                return COMEBACK;
            case SET_QUESTION:
                deleteAll(uptMess);
                if (hasMessageText()) {
                    citizensRegistration.setQuestion(updateMessageText);
                    uptMess = toDeleteKeyboard(sendMessageWithKeyboard(getText(1024), 1005));
                    waitingType = WaitingType.WAIT;
                }
                return COMEBACK;
            case WAIT:
                deleteAll(uptMess);
                if (isButton(19)) {
                    list2 = new ArrayList<>();
                    citizensRegistration.setDate(new Date());
                    citizensRegistrationDao.insert(citizensRegistration);
                    try {
                        sendMessage(String.format(getText(1074), Optional.ofNullable(userDao.getUserByChatId(chatId).getFullName()).orElseThrow(() -> new Exception("User not found")), receptionType.getName()));
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                    sendMessEmp();
                    return EXIT;
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private void sendMessEmp() {
        receptionEmployees = Optional.ofNullable(receptionEmployeeDao.getReceptionEmployeeById(citizensRegistration.getReceptionId())).map(recEs -> {
            for (ReceptionEmployee e : recEs) {
                try {

                    sendMessage(String.format(getText(1073), Optional.ofNullable(userDao.getUserByChatId(chatId).getFullName()).orElseThrow(() -> new Exception("User not found"))), e.getChatId());
                } catch (Exception telegramApiException) {
                    log.error(telegramApiException.getMessage());
                }
            }
            return recEs;
        }).orElse(new ArrayList<>());
    }

    private void sendMessWKey(int id, ButtonsLeaf buttonsLeaf) throws TelegramApiException {
        uptMess = toDeleteMessage(sendMessageWithKeyboard(getText(id), buttonsLeaf.getListButton()));
    }

    private void sendMess(int id) throws TelegramApiException {
        uptMess =toDeleteMessage(sendMessage(id));
    }
}
