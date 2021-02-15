package com.alatauBot.services;

import com.alatauBot.dao.DaoFactory;
import com.alatauBot.dao.impl.UserDao;
import com.alatauBot.dao.impl.ButtonDao;
import com.alatauBot.dao.impl.ReceptionEmployeeDao;
import com.alatauBot.entity.custom.CitizensRegistration;
import com.alatauBot.entity.custom.ReceptionEmployee;
import com.alatauBot.entity.custom.ReceptionType;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.utils.BotUtil;
import com.alatauBot.utils.UpdateUtil;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.poi.ss.formula.eval.ErrorEval.getText;

public class Citizens {

    private BotUtil botUtil;
    private Update update;
    private long chatId;
    private DaoFactory factory = DaoFactory.getInstance();
    private ButtonDao buttonDao = factory.getButtonDao();
    private UserDao userDao = factory.getUserDao();
    private List<ReceptionEmployee> receptionEmployees;
    private CitizensRegistration citizensRegistration = new CitizensRegistration();
    private SimpleDateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd");
    private ReceptionEmployeeDao receptionEmployeeDao = factory.getReceptionEmployeeDao();
    private WaitingType waitingType = WaitingType.START;

    public boolean Sitizens(Update update, BotUtil botUtil, CitizensRegistration citizensRegistration, List<ReceptionType> receptions, ReceptionType receptionType, String messageText, List<String> list2) throws TelegramApiException {
        if (chatId == 0 || botUtil == null) {
            chatId = UpdateUtil.getChatId(update);
            this.botUtil = botUtil;
            this.update = update;
        }
        switch (waitingType) {
            case START:
                citizensRegistration.setChatId(chatId);
                citizensRegistration.setReceptionId(receptions.get(1).getId());
                citizensRegistration.setStatus("Записан");
                citizensRegistration.setCitizensDate(dateFormat.format(new Date()));
                citizensRegistration.setCitizensTime(factory.getCitizensInfoDao().getById(receptionType.getId()).getTime());
                getIin();
                waitingType = WaitingType.SET_IIN;
                return false;
            case SET_IIN:
                citizensRegistration.setIin(update.getMessage().getText());
                getQuest();
                waitingType = WaitingType.SET_QUESTION;
                return false;
//                try {
//                    Long.parseLong(update.getMessage().getText());
//                } catch (NumberFormatException e) {
//                    wrongIinNotNumber();
//                    getIin();
//                    return false;
//                }
//                if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().length() == 12) {
//                    citizensRegistration.setIin(update.getMessage().getText());
//                    getQuest();
//                    waitingType = WaitingType.SET_QUESTION;
//                } else {
//                    getIin();
//                }
            case SET_QUESTION:
                if (update.getMessage().hasText()) {
                    citizensRegistration.setQuestion(update.getMessage().getText());
                    botUtil.sendMessageWithKeyboard("<i><b>Подтвердите</b></i>", factory.getKeyboardMarkUpDao().select(1005), chatId);
                    waitingType = WaitingType.WAIT;
                }
                return false;
            case WAIT:
                if (isButton(19)) {
                    list2 = new ArrayList<>();
                    citizensRegistration.setDate(new Date());
                    factory.getCitizensRegistrationDao().insert(citizensRegistration);
                    receptionEmployees = receptionEmployeeDao.getReceptionEmployeeById(citizensRegistration.getReceptionId());
                    receptionEmployees.forEach((e) -> {
                        try {
                            botUtil.sendMessage("lol11", e.getChatId());
                        } catch (TelegramApiException telegramApiException) {
                            telegramApiException.printStackTrace();
                        }
                    });
                    botUtil.sendMessage(String.format(getText(1007), userDao.getUserByChatId(chatId).getFullName(), receptionType.getName()), chatId);
                    return true;
                }
                return false;
        }
        return true;
    }

    private int getIin() throws TelegramApiException {
        return botUtil.sendMessage(1004, chatId);
    }

    private int wrongIinNotNumber() throws TelegramApiException {
        return botUtil.sendMessage(1005, chatId);
    }

    private int getQuest() throws TelegramApiException {
        return botUtil.sendMessage(1003, chatId);
    }

    private boolean isButton(int buttonId) {
        return update.getMessage().getText().equals(buttonDao.getButtonText(buttonId));
    }
}
