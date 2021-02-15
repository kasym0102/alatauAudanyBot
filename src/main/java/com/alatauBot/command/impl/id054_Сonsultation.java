package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.custom.AppealTeg;
import com.alatauBot.entity.custom.AppealTegQuestionAndOption;
import com.alatauBot.entity.custom.AppealType;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.utils.ButtonsLeaf;
import com.alatauBot.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class id054_Сonsultation extends Command {

    private List<AppealType>                    appealTypes;
    private List<AppealTeg>                     appealTegs;
    private List<AppealTegQuestionAndOption>    appealTegQuestionAndOptions;
    private ButtonsLeaf buttonsLeaf;
    private int                                 typeId, tegId, updMes;
    private List<String>                        appealTypeName, appealTegName, appealTegQuestionAnswer;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        switch (waitingType) {
            case START:
                deleteAll(updMes);
                getTypeMenu();
                waitingType = WaitingType.SET_TYPE;
                return COMEBACK;
            case SET_TYPE:
                deleteAll(updMes);
                if (hasCallbackQuery()) {
                    typeId = Integer.parseInt(updateMessageText);
                    getTegMenu();
                    waitingType = WaitingType.SET_TEG;
                }else {
                    getTypeMenu();
                    waitingType = WaitingType.SET_TYPE;
                }
                return COMEBACK;
            case SET_TEG:
                deleteAll(updMes);
                if (hasCallbackQuery()) {
                    if (appealTegName.get(Integer.parseInt(updateMessageText)).contains(getButtonText(31))){
                        getTypeMenu();
                        waitingType = WaitingType.SET_TYPE;
                    }else {
                        tegId = appealTegs.get(Integer.parseInt(updateMessageText)).getId();
                        getAppealTegOrQuestion();
                        waitingType = WaitingType.SET_TEXT_DESC;
                    }
                    return COMEBACK;
                }else {
                    getTegMenu();
                    waitingType = WaitingType.SET_TEG;
                }
                return COMEBACK;
            case SET_TEXT_DESC:
                deleteAll(updMes);
                if (hasCallbackQuery()){
                    if (appealTegQuestionAnswer.get(Integer.parseInt(updateMessageText)).contains(getButtonText(31))){
                        getTegMenu();
                        waitingType = WaitingType.SET_TEG;
                        return COMEBACK;
                    }else {
                        StringBuilder stringBuilder = new StringBuilder();
                        AppealTegQuestionAndOption appQueOrAns = new AppealTegQuestionAndOption();
                        appQueOrAns = appealTegQuestionAndOptions.get(Integer.parseInt(updateMessageText));
                        stringBuilder.append(appQueOrAns.getQuestion()).append(next).append(next);
                        stringBuilder.append(appQueOrAns.getAnswer());
                        updMes = toDeleteMessage(sendMessageWithKeyboard(stringBuilder.toString(), 1006));
                        waitingType = WaitingType.SET_TEXT_DESC_BACK;
                        return COMEBACK;
                    }
                }else {
                    getAppealTegOrQuestion();
                    waitingType = WaitingType.SET_TEXT_DESC;
                    return COMEBACK;
                }
            case SET_TEXT_DESC_BACK:
                deleteAll(updMes);
                if (hasCallbackQuery()){
                    getAppealTegOrQuestion();
                    waitingType = WaitingType.SET_TEXT_DESC;
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private void getAppealTegOrQuestion() throws TelegramApiException {
        appealTegQuestionAnswer = new ArrayList<>();
        appealTegQuestionAndOptions = Optional.ofNullable(appealTegQuestionAndOptionDao.getAppealQuestionOrAnswer(tegId)).map(ap112 -> { ap112.forEach(ap -> appealTegQuestionAnswer.add(ap.getQuestion())); return ap112; }).orElse(new ArrayList<>());
        appealTegQuestionAnswer.add(getButtonText(Const.BACK));
        buttonsLeaf = new ButtonsLeaf(appealTegQuestionAnswer);
        updMes = toDeleteMessage(sendMessageWithKeyboard(getText(1027), buttonsLeaf.getListButton()));
    }

    private void getTegMenu() throws TelegramApiException {
        appealTegName = new ArrayList<>();
        appealTegs = Optional.ofNullable(appealTegDao.appealTegList(appealTypes.get(typeId).getId())).map(ap112 -> { ap112.forEach(ap -> appealTegName.add(ap.getName())); return ap112; }).orElse(new ArrayList<>());
        appealTegName.add(getButtonText(Const.BACK));
        buttonsLeaf = new ButtonsLeaf(appealTegName);
        updMes = toDeleteMessage(sendMessageWithKeyboard(getText(1021), buttonsLeaf.getListButton()));
    }

    private void getTypeMenu() throws TelegramApiException {
        appealTypeName = new ArrayList<>();
        appealTypes = Optional.ofNullable(appealTypeDao.getAppealTypes()).map(appealTypes112 -> { appealTypes112.forEach(appealType -> appealTypeName.add(appealType.getName())); return appealTypes112; }).orElse(new ArrayList<>());
        buttonsLeaf = new ButtonsLeaf(appealTypeName);
        updMes = toDeleteMessage(sendMessageWithKeyboard(getText(1020), buttonsLeaf.getListButton()));
    }
}
