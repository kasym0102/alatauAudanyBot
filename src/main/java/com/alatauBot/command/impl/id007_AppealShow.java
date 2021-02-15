package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.custom.*;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.entity.standart.User;
import com.alatauBot.utils.ButtonsLeaf;
import com.alatauBot.utils.Const;
import com.alatauBot.utils.components.IKeyboardOld;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class id007_AppealShow extends Command {

    private List<String> listTypeName, listTegName;
    private List<AppealType> appealTypes = new ArrayList<>();
    private List<AppealTeg> appealTegs;
    private String buttonName = buttonDao.getButtonText(Const.BACK);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private AppealMap appealMap;
    private AppealEmployee appealEmployee;
    private AppealTeg appealTeg;
    private AppealType appealType;
    private AppealTask appealTask;
    private ButtonsLeaf buttonsLeaff;
    private ButtonsLeaf buttonsLeaf;
    private int upMessId, tegEmpId, curId;
    private Date dateBegin;
    private List<AppealEmployee> allAppealEmployee;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                appealTypes = factory.getAppealTypeDao().getAppealTypes();
                if (appealTypes == null || appealTypes.size() == 0) {
                    sendMessage(1049);
                    return EXIT;
                }
                listTypeName = new ArrayList<>();
                appealTypes.forEach(appealType1 -> listTypeName.add(appealType1.getName()));
                buttonsLeaf = new ButtonsLeaf(listTypeName);
                sendMessKeyBoar(buttonsLeaf, 1020);
                waitingType = WaitingType.SET_TYPE;
                return COMEBACK;
            case SET_TYPE:
                deleteAll(upMessId);
                if (hasCallbackQuery()) {
                    dateBegin = new Date();
                    appealTask = new AppealTask();
                    appealMap = new AppealMap();
                    listTegName = new ArrayList<>();
                    curId = Integer.parseInt(updateMessageText);
                    appealType = appealTypes.get(curId);
                    appealTegs = Optional.ofNullable(appealTegDao.appealTegList(appealType.getId())).map(appealTegs-> {
                        appealTegs.forEach(a -> listTegName.add(a.getName()));
                        return appealTegs;
                    }).orElse(new ArrayList<>());
                    listTegName.add(buttonName);
                    appealTask.setIdStatus(3);
                    appealTask.setChatId(chatId);
                    appealTask.setDataBegin(dateBegin);
                    appealMap.setAppealTypeId(appealType.getId());
                    appealTask.setAppealTypeId(appealType.getId());
                    appealTask.setReceptionTypeId(appealType.getReceptionTypeId());
                    buttonsLeaff = new ButtonsLeaf(listTegName);
                    sendMessKeyBoar(buttonsLeaff, 1027);
                    waitingType = WaitingType.SET_TEG;
                } else {
                    sendMessKeyBoar(buttonsLeaf, 1020);
                }
                return COMEBACK;
            case SET_TEG:
                deleteAll(upMessId);
                if (listTegName.get(Integer.parseInt(updateMessageText)).equals(buttonName)) {
                    sendMessKeyBoar(buttonsLeaf, 1020);
                    waitingType = WaitingType.SET_TYPE;
                    return COMEBACK;
                }
                if (hasCallbackQuery()) {
                    appealTeg = appealTegs.get(Integer.parseInt(updateMessageText));
                    appealMap.setAppealTegId(appealTegs.get(Integer.parseInt(updateMessageText)).getId());
                    appealMap.setReceptionTypeId(appealTypes.get(curId).getReceptionTypeId());
                    appealTask.setAppealTegId(appealTeg.getId());
                    upMessId = sendMessage(Optional.ofNullable(appealTeg.getName()).map(s -> s + next + next + getText(1003)).orElse(getText(1003)));
                    waitingType = WaitingType.SET_TEXT;
                }
                return COMEBACK;
            case SET_TEXT:
                deleteAll(upMessId);
                if (hasMessageText()) {
                    appealTask.setText(updateMessageText);
                    User user = Optional.ofNullable(userDao.getUserByChatId(chatId)).orElse(new User());
                    appealTask.setFullName(user.getId());
                    appealTask.setPhone(user.getPhone());
                    sendMess(1009);
                    waitingType = WaitingType.SET_PHOTO_OR_VIDEO;
                } else {
                    sendMess(1003);
                }
                return COMEBACK;
            case SET_PHOTO_OR_VIDEO:
                deleteAll(upMessId);
                if (update.hasMessage()) {
                    if (update.getMessage().getPhoto() != null) {
                        appealTask.setPhoto(updateMessagePhoto);
                        sendMess(1008);
                        waitingType = WaitingType.SET_LOCATION;
                    } else if (update.getMessage().hasVideo()) {
                        appealTask.setVideo(update.getMessage().getVideo().getFileId());
                        sendMess(1008);
                        waitingType = WaitingType.SET_LOCATION;
                    } else if (isButton(54)) {
                        sendMess(1008);
                        waitingType = WaitingType.SET_LOCATION;
                    } else {
                        sendMess(1009);
                    }
                } else {
                    sendMess(1009);
                }
                return COMEBACK;
            case SET_LOCATION:
                deleteAll(upMessId); int taskId;
                if (update.getMessage().hasLocation()) {
                    StringBuilder employeeName = new StringBuilder();
                    appealTask.setLocation(update.getMessage().getLocation().getLatitude()+Const.HESH+update.getMessage().getLocation().getLongitude());

                    taskId = Optional.ofNullable(appealTaskDao.insertAppealTask(appealTask)).orElse(0);

                    allAppealEmployee = Optional.ofNullable(appealEmployeeDao.getAllByTegId(appealTeg.getId())).map(appealEmployees -> {
                        appealEmployees.forEach(e -> employeeName.append(Optional.ofNullable(userDao.getUserByChatId(e.getChatId()).getFullName()).orElse("user"))); return appealEmployees;
                    }).orElse(new ArrayList<>());
                    String fullName = null;
                    try { fullName = Optional.ofNullable(userDao.getUserByChatId(appealTask.getChatId()).getFullName()).orElseThrow(() -> new Exception("User not found"));}
                    catch (Exception e) { log.error(e.getMessage()); }
                    sendMessageToUser(appealTask, taskId, employeeName, fullName);
                    sendMessageToEmployee(fullName, dateBegin, appealTask.getText(), taskId, employeeName);
                    return EXIT;
                } else if (updateMessageText.equals("/next")) {
                    StringBuilder sb = new StringBuilder();
                    Random random = new Random();
                    appealTask.setLocation(sb.append(String.format("43.%s65545", random.nextInt(9))).append(Const.HESH).append(String.format("76.%s3742", random.nextInt(9))).toString());

                    taskId = appealTaskDao.insertAppealTask(appealTask);


                    StringBuilder employeeName = new StringBuilder();
                    allAppealEmployee = appealEmployeeDao.getAllByTegId(appealTeg.getId());
                    allAppealEmployee.forEach(e -> employeeName.append(userDao.getUserByChatId(e.getChatId()).getFullName()).append(next));
                    String fullName = userDao.getUserByChatId(appealTask.getChatId()).getFullName();
                    sendMessageToUser(appealTask, taskId, employeeName, fullName);
                    sendMessageToEmployee(fullName, dateBegin, appealTask.getText(), taskId, employeeName);
                    return EXIT;
                } else {
                    sendMess(1008);
                    return COMEBACK;
                }
        }
        return EXIT;
    }

    private void sendMess(int id) throws TelegramApiException { upMessId = toDeleteMessage(sendMessage(id)); }

    private void sendMessKeyBoar(ButtonsLeaf buttonsLeaf, int id) throws TelegramApiException { upMessId = toDeleteKeyboard(sendMessageWithKeyboard(id, buttonsLeaf.getListButton())); }

    private void sendMessageToUser(AppealTask appealTask, int taskId, StringBuilder employeeName, String name) throws TelegramApiException {
        StringBuilder messageToUser = new StringBuilder();
        messageToUser.append(getText(2012)).append(next).append(next);
        messageToUser.append(getText(1052)).append(name).append(next);
        messageToUser.append(getText(1049) + taskId).append(next);
        messageToUser.append(getText(1050)).append(employeeName.toString()).append(next);
        messageToUser.append(getText(1061)).append("В процессе").append(next);
        messageToUser.append(getText(1053)).append(appealTask.getText());
//        sendMessageWithKeyboard(messageToUser.toString(), 17); // 17- клавиатура "написать коммент"
        sendMessageWithKeyboard(messageToUser.toString(), 3);
    }

    private void sendMessageToEmployee(String name, Date dateBegin, String text, int taskId, StringBuilder employeeName) {
        StringBuilder messageToEmployee = new StringBuilder();
        String deadlines = dateFormat.format(dateBegin);
        messageToEmployee.append(getText(1049) + taskId).append(next);
        messageToEmployee.append(getText(1052)).append(name).append(next);
        messageToEmployee.append(getText(1053)).append(text).append(next);
        messageToEmployee.append(getText(1054)).append(deadlines).append(next);
        messageToEmployee.append(getText(1050)).append(employeeName);
        for (AppealEmployee employee : allAppealEmployee) {
            long directId = employee.getChatId();
            IKeyboardOld kb = new IKeyboardOld();
            kb.next();

            try {
                sendMessage(messageToEmployee.toString(), directId);
            } catch (TelegramApiException e) { }

            try {
                sendMessage(String.format(getText(2011), taskId), employee.getChatId());
                if (Optional.ofNullable(appealTask.getPhoto()).isPresent()) bot.execute(new SendPhoto().setChatId(directId).setPhoto(appealTask.getPhoto())).getMessageId();
                if (Optional.ofNullable(appealTask.getVideo()).isPresent()) bot.execute(new SendVideo().setChatId(directId).setVideo(appealTask.getVideo())).getMessageId();
                SendLocation sendLocation = new SendLocation().setLatitude(Float.parseFloat(appealTask.getLocation().split(Const.HESH)[0])).setLongitude(Float.parseFloat(appealTask.getLocation().split(Const.HESH)[1]));
                bot.execute(sendLocation.setChatId(directId)).getMessageId();
            } catch (TelegramApiException e) { }

        }

    }
}
