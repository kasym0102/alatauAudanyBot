package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.custom.AppealTeg;
import com.alatauBot.entity.custom.AppealTegQuestionAndOption;
import com.alatauBot.entity.enums.Language;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.services.LanguageService;
import com.alatauBot.utils.ButtonsLeaf;
import com.alatauBot.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class id055_EditСonsultation extends Command {

    private List<AppealTeg> appealTegs;
    private AppealTegQuestionAndOption appealTegQuestionAndOption;
    private List<AppealTegQuestionAndOption> appealTegQuestionAndOptions;
    private List<String> list1, list2;
    private int tegId, queOrOptId, uptMes, i;
    private ButtonsLeaf buttonsLeaf;
    private StringBuilder stringBuilder;
    private String icon;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        switch (waitingType) {
            case START:
                deleteAll(uptMes);
                getTegMenu();
                waitingType = WaitingType.SET_TEG;
                return COMEBACK;
            case SET_TEG:
                deleteAll(uptMes);
                queOrOptId = appealTegs.get(onlyNumbers(updateMessageText) - 1).getId();
                if (list1.contains(updateMessageText)) {
                    getQuestion();
                    waitingType = WaitingType.SET_QUESTION;
                }
                return COMEBACK;
            case SET_QUESTION:
                deleteAll(uptMes);
                if (updateMessageText.equals("/swap")) {
                    if (LanguageService.getLanguage(chatId) == Language.ru) {
                        LanguageService.setLanguage(chatId, Language.kz);
                        icon = "\uD83C\uDDF0\uD83C\uDDFF";
                    } else {
                        LanguageService.setLanguage(chatId, Language.ru);
                        icon = "🇷🇺";
                    }
                    getQuestion();
                    waitingType = WaitingType.SET_QUESTION;
                }else if (list2.contains(updateMessageText)){
                    appealTegQuestionAndOptionDao.deleteQuestion(appealTegQuestionAndOptions.get(onlyNumbers(updateMessageText) - 1).getId());
                    getQuestion();
                    waitingType = WaitingType.SET_QUESTION;
                }else if (updateMessageText.equals("/newQues")) {
                    uptMes = sendMessage(1084);
                    waitingType = WaitingType.SET_QUESTION_TEXT;
                    i = 1;
                } else if (updateMessageText.equals(Const.BAC)) {
                    getTegMenu();
                    waitingType = WaitingType.SET_TEG;
                } else if (list1.contains(updateMessageText)) {
                    uptMes = toDeleteMessage(sendMessage(appealTegQuestionAndOptions.get(onlyNumbers(updateMessageText) - 1).getAnswer() + next + next + Const.BAC));
                    waitingType = WaitingType.SET_QUESTION_BACK;
                }
                return COMEBACK;
            case SET_QUESTION_BACK:
                deleteAll(uptMes);
                if (updateMessageText.equals(Const.BAC)) {
                    getQuestion();
                    waitingType = WaitingType.SET_QUESTION;
                }
                return COMEBACK;
            case SET_QUESTION_TEXT:
                deleteAll(uptMes);
                if (i == 1) {
                    appealTegQuestionAndOption = new AppealTegQuestionAndOption();
                    appealTegQuestionAndOption.setQuestion(updateMessageText);
                    appealTegQuestionAndOption.setLangId(1);
                    uptMes = sendMessage(1085);
                    i = 2;
                } else if (i == 2) {
                    appealTegQuestionAndOption.setAnswer(updateMessageText);
                    appealTegQuestionAndOption.setAppealTegId(queOrOptId);
                    appealTegQuestionAndOption.setId(appealTegQuestionAndOptionDao.insertQuestion(appealTegQuestionAndOption));
                    uptMes = sendMessage(1086);
                    i = 3;
                } else if (i == 3) {
                    appealTegQuestionAndOption.setQuestion(updateMessageText);
                    appealTegQuestionAndOption.setLangId(2);
                    uptMes = sendMessage(1087);
                    i = 4;
                } else if (i == 4) {
                    appealTegQuestionAndOption.setAnswer(updateMessageText);
                    appealTegQuestionAndOptionDao.insertQuestion(appealTegQuestionAndOption);
                    getQuestion();
                    waitingType = WaitingType.SET_QUESTION;
                    return COMEBACK;
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private void getQuestion() throws TelegramApiException {
        stringBuilder = new StringBuilder();
        list1 = new ArrayList<>(); list2 = new ArrayList<>();
        i = 0;
        appealTegQuestionAndOptions = Optional.ofNullable(appealTegQuestionAndOptionDao.getAppealQuestionOrAnswer(queOrOptId)).map(appQuOrOps -> {
            appQuOrOps.forEach(appQuOrOp -> {
                stringBuilder.append("/ans").append(++i).append(hyphen).append(Const.DELL).append(i).append(space).append(appQuOrOp.getQuestion()).append(next);
                list1.add("/ans" + i); list2.add(Const.DELL + i);
            });
            return appQuOrOps;
        }).orElse(new ArrayList<>());
        swapLang();
        uptMes = toDeleteMessage(sendMessage(String.format(getText(1080), icon, stringBuilder.toString())));
    }

    private void getTegMenu() throws TelegramApiException {
        stringBuilder = new StringBuilder();
        list1 = new ArrayList<>();
        i = 0;
        appealTegs = Optional.ofNullable(appealTegDao.getAll()).map(appealTegs1 -> {
            appealTegs1.forEach(appealTeg -> {
                stringBuilder.append("/ques").append(++i).append(hyphen).append(appealTeg.getName()).append(next);
                list1.add("/ques" + i);
            });
            return appealTegs1;
        }).orElse(new ArrayList<>());
        uptMes = toDeleteMessage(sendMessage(stringBuilder.toString()));
    }

    private void swapLang(){
        if (LanguageService.getLanguage(chatId)==Language.ru) icon = "🇷🇺";
        if (LanguageService.getLanguage(chatId)==Language.kz) icon = "\uD83C\uDDF0\uD83C\uDDFF";
    }
}
