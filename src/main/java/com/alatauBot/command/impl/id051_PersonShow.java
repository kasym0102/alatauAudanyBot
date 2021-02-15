package com.alatauBot.command.impl;


import com.alatauBot.command.Command;
import com.alatauBot.entity.custom.AppealEmployee;
import com.alatauBot.entity.custom.AppealTask;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.utils.ButtonsLeaf;
import com.alatauBot.utils.Const;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class id051_PersonShow extends Command {

    private List<AppealTask> appealTasks;
    private ButtonsLeaf buttonsLeaf;
    private List<String> list;
    private String statusName;
    private int uptMes, statusIdNum;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        if (!isEmployeeCategory()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType){
            case START:
                deleteMessage(updateMessageId);
                if (isButton(120)) {
                    statusIdNum = 1;
                    statusName = "Выполненные";
                }
//                if (isButton(121)) {
//                    statusIdNum = 2;
//                    statusName = "Не выполненные";
//                }
                if (isButton(122)) {
                    statusIdNum = 3;
                    statusName = "В процессе";
                }
                if (isButton(127)) {
                    statusIdNum = 4;
                    statusName = "Просрочные";
                }
                list = new ArrayList<>();
                appealTasks = new ArrayList<>();

                List<AppealEmployee> employees = appealEmployeeDao.getAllByChatId(chatId); // тегтарга жауап беретын озынын списогы



//                for (AppealEmployee appealEmployee: employees) {
//                    appealTasks.addAll (Optional.ofNullable(appealTaskDao.getAllByStatusAndAppealTegId(statusIdNum, appealEmployee.getTegId())).map(aps -> {
//                        aps.forEach(ap -> list.add("№ " + String.valueOf(ap.getId())));
//                        return aps;
//                    }).orElse(new ArrayList<>()));
//                }

                 for (AppealEmployee appealEmployee: employees) {
                    appealTasks.addAll (appealTaskDao.getAllByStatusAndAppealTegId(statusIdNum, appealEmployee.getTegId()));
                }

                sort(appealTasks);

                for (AppealTask appealTask: appealTasks) {
                    list.add("№ " + String.valueOf(appealTask.getId()));
                }

                buttonsLeaf = new ButtonsLeaf(list);

//                appealTasks = appealTaskDao.getAll();

                uptMes = toDeleteMessage(sendMessageWithKeyboard(statusName, buttonsLeaf.getListButton()));
                waitingType = WaitingType.CHOICE_OPTION;
                 return COMEBACK;
            case CHOICE_OPTION:
                deleteAll(uptMes);
                if (hasCallbackQuery()) {
                    AppealTask appealTask = appealTasks.get(Integer.parseInt(updateMessageText));
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(getText(1049)).append(appealTask.getId()).append(next);
                    try {
//                        stringBuilder.append(getText(1050)).append(Optional.ofNullable(userDao.getUserByChatId(appealTask.getChatId()).getFullName()).orElseThrow(() -> new Exception("User not found"))).append(next);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }

                    //todo add name of sender
                    stringBuilder.append(getText(2003)).append(userDao.getFullNameByChatId(appealTask.getChatId())).append(next);
                    // done
                    stringBuilder.append(getText(1054)).append(appealTask.getDataBegin()).append(next);
                    stringBuilder.append(getText(2007)).append(appealTask.getDataDeadline()).append(next);
                    stringBuilder.append(getText(1053)).append(appealTask.getText());
                    if (statusIdNum == 1 || statusIdNum == 5){
                        uptMes = toDeleteKeyboard(sendMessageWithKeyboard(stringBuilder.toString(),103));
                    }else {
                        try {
                            sendMessage(String.format(getText(2011), appealTask.getId())); //, employee.getChatId()
                            if (Optional.ofNullable(appealTask.getPhoto()).isPresent()) bot.execute(new SendPhoto().setChatId(chatId).setPhoto(appealTask.getPhoto())).getMessageId();
                            if (Optional.ofNullable(appealTask.getVideo()).isPresent()) bot.execute(new SendVideo().setChatId(chatId).setVideo(appealTask.getVideo())).getMessageId();
                            SendLocation sendLocation = new SendLocation().setLatitude(Float.parseFloat(appealTask.getLocation().split(Const.HESH)[0])).setLongitude(Float.parseFloat(appealTask.getLocation().split(Const.HESH)[1]));
                            bot.execute(sendLocation.setChatId(chatId)).getMessageId();
                        } catch (TelegramApiException e) { }

//                        uptMes = sendMessage(stringBuilder.toString());

//                        toDeleteKeyboard(sendMessageWithKeyboard(104));

                        toDeleteKeyboard(sendMessageWithKeyboard(stringBuilder.toString(),104));
                    }
                    waitingType=WaitingType.CHOOSE_BACK;
                }
                return COMEBACK;
            case CHOOSE_BACK:
                deleteAll(uptMes);
                if(isButton(124)){
                    uptMes = toDeleteKeyboard(sendMessageWithKeyboard("Выполненные", buttonsLeaf.getListButton()));
                    waitingType = WaitingType.CHOICE_OPTION;
                }
                return COMEBACK;
        }
        return EXIT;
    }
}