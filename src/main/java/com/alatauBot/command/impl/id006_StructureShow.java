package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.custom.*;
import com.alatauBot.entity.enums.ParseMode;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.entity.standart.User;
import com.alatauBot.utils.ButtonsLeaf;
import com.alatauBot.utils.Const;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
public class id006_StructureShow extends Command {
    private List<DepartmentsInfo> departmentsInfos;
    private List<DepartmentsType> departmentsTypes;
    private List<ReceptionType> receptionTypes;
    private CitizensRegistration citizensRegistration;
    private List<String> list, listDep = new ArrayList<>(), list2;
    private DepartmentsInfo departmentsInfo;
    private ReceptionInfo receptionInfo;
    private List<ReceptionEmployee> receptionEmployees;
    private ReceptionType receptionType;
    private ButtonsLeaf menuReceptionTypes, menuDepartmentTypes;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private int levelId;
    private int messageIdPhoto, uptMess;
    private int curId;
    private int backId;


    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        switch (waitingType) {
            case START:
                deleteAll(uptMess);
                menuReceptionType();
                waitingType = WaitingType.SET_RECEPTION_TYPE;
                return COMEBACK;
            case SET_RECEPTION_TYPE:
                deleteAll(uptMess);
                receptionType = receptionTypes.get(Integer.parseInt(updateMessageText));
                if (list.get(Integer.parseInt(updateMessageText)).contains(receptionTypeDao.getReceptionText(8))) {
                    menuDepartmentType();
                    waitingType = WaitingType.CHOOSE_DEPARTMENTS;
                    return COMEBACK;
                } else {
                    curId = receptionTypes.get(Integer.parseInt(updateMessageText)).getReceptionId();
                    receptionInfo = receptionInfoDao.getReceptionId(receptionTypes.get(Integer.parseInt(updateMessageText)).getId());
                    receptionInfo();
                    waitingType = WaitingType.CHOOSE_BACK;
                    return COMEBACK;
                }
            case CHOOSE_DEPARTMENTS:
                deleteAll(uptMess);
                if (list.get(Integer.parseInt(updateMessageText)).equals(buttonDao.getButtonText(Const.BACK))) {
                    menuReceptionType();
                    waitingType = WaitingType.SET_RECEPTION_TYPE;
                    return COMEBACK;
                } else {
                    departmentsTypes = departmentTypeDao.getAll();
                    departmentsInfo = departmentsInfoDao.getOneById(departmentsTypes.get(Integer.parseInt(updateMessageText)).getId());
                    try {
                        departmentInfo();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                    waitingType = WaitingType.SET_STRUCTURE_DEPARTMENTS_INFO;
                    return COMEBACK;
                }
            case SET_STRUCTURE_DEPARTMENTS_INFO:
                deleteAll(uptMess);
                if (isButton(31)) {
                    menuDepartmentType();
                    waitingType = WaitingType.CHOOSE_DEPARTMENTS;
                    return COMEBACK;
                } else if (isButton(33)) {
                    uptMess = sendMessage(1022);
                    backId = 10;
                    waitingType = WaitingType.EDIT_CATEGORY;
                    return COMEBACK;
                }
            case EDIT_CATEGORY:
                deleteAll(uptMess);
                if (updateMessageText.equals("/editName")) {
                    uptMess = sendMessage(1021);
                    if (backId == 10) {
                        backId = 12;
                        waitingType = WaitingType.EDIT_CATEGORY2;
                        return COMEBACK;
                    } else {
                        backId = 8;
                        waitingType = WaitingType.EDIT_CATEGORY2;
                        return COMEBACK;
                    }
                }
                if (updateMessageText.equals("/editPhoto")) {
                    messageIdPhoto = sendMessage(1023);
                    if (backId == 10) {
                        backId = 13;
                        waitingType = WaitingType.EDIT_CATEGORY2;
                        return COMEBACK;
                    } else {
                        backId = 9;
                        waitingType = WaitingType.EDIT_CATEGORY2;
                        return COMEBACK;
                    }
                }
            case EDIT_CATEGORY2:
                deleteAll(uptMess);
                if (backId == 8) {
                    receptionInfo.setText(updateMessageText);
                    receptionInfoDao.updateInfoText(receptionInfo);
                    try {
                        receptionInfo();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                    waitingType = WaitingType.CHOOSE_BACK;
                    return COMEBACK;
                } else if (backId == 9) {
                    receptionInfo.setPhoto(updateMessagePhoto);
                    receptionInfoDao.updateInfoPhoto(receptionInfo);
                    try {
                        receptionInfo();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                    waitingType = WaitingType.CHOOSE_BACK;
                    return COMEBACK;
                } else if (backId == 12) {
                    departmentsInfo.setText(updateMessageText);
                    departmentsInfoDao.updateDepartmentsInfoText(departmentsInfo);
                    try {
                        departmentInfo();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                    waitingType = WaitingType.SET_STRUCTURE_DEPARTMENTS_INFO;
                    return COMEBACK;
                } else if (backId == 13) {
                    departmentsInfo.setPhoto(updateMessagePhoto);
                    departmentsInfoDao.updateDepartmentsInfoPhoto(departmentsInfo);
                    try {
                        departmentInfo();
                    } catch (Exception e) { log.error(e.getMessage()); }
                    waitingType = WaitingType.SET_STRUCTURE_DEPARTMENTS_INFO;
                    return COMEBACK;
                }

            case CHOOSE_BACK:
                deleteAll(uptMess);
                if (backId == 1) {
                    if (isButton(31)) {
                        menuReceptionType();
                        waitingType = WaitingType.SET_RECEPTION_TYPE;
                        return COMEBACK;
                    } else if (isButton(33) || levelId == 123) {
                        uptMess = sendMessage(1022);
                        waitingType = WaitingType.EDIT_CATEGORY;
                        return COMEBACK;
                    } else if (isButton(32)) {
                        citizensRegistration = new CitizensRegistration();
                        citizensRegistration.setChatId(chatId);
                        citizensRegistration.setReceptionId(receptionType.getId()); // TODO: RECEPTION TYPE FIX
                        citizensRegistration.setStatus("Записан");
                        citizensRegistration.setCitizensDate(dateFormat.format(new Date()).split("-")[0] + "-" + dateFormat.format(new Date()).split("-")[1] + "-" + "пятница");
                        citizensRegistration.setCitizensTime(factory.getCitizensInfoDao().getById(receptionType.getId()).getTime());
                        sendMess(1004);
                        waitingType = WaitingType.SET_IIN;
                        return COMEBACK;
                    }
                }
            case SET_IIN:
                deleteAll(uptMess);
                try {
                    Long.parseLong(updateMessageText);
                    if (updateMessageText.length() == 12) {
                        citizensRegistration.setIin(updateMessageText);
                        sendMess(1003);
                        waitingType = WaitingType.SET_QUESTION;
                    } else {
                        sendMess(1005);
                        sendMess(1004);
                    }
                } catch (Exception e) {
                    sendMess(1005);
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
                    citizensRegistration.setDate(new Date());
                    citizensRegistrationDao.insert(citizensRegistration);
                    sendMessage(String.format(getText(1074), userDao.getUserByChatId(chatId).getFullName(), receptionType.getName()));
                    sendMessEmp();
                    return EXIT;
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private void menuReceptionType() throws TelegramApiException {
        list = new ArrayList<>();
        receptionTypes = Optional.ofNullable(receptionTypeDao.getAllType()).map(recTyps -> {
            recTyps.forEach(recTyp -> list.add(recTyp.getName()));
            menuReceptionTypes = new ButtonsLeaf(list);
            return recTyps;
        }).orElse(new ArrayList<>());
        uptMess = toDeleteMessage(sendMessageWithKeyboard(1020, menuReceptionTypes.getListButton()));
    }

    private void menuDepartmentType() throws TelegramApiException {
        list = new ArrayList<>();
        departmentsTypes = Optional.ofNullable(departmentTypeDao.getAll()).map(depTyps -> {
            depTyps.forEach(depTyp -> list.add(depTyp.getName()));
            list.add(buttonDao.getButtonText(31));
            menuDepartmentTypes = new ButtonsLeaf(list);
            return depTyps;
        }).orElse(new ArrayList<>());
        uptMess = toDeleteKeyboard(sendMessageWithKeyboard(1020, menuDepartmentTypes.getListButton()));
    }

    private int receptionInfo() {
        Optional.ofNullable(receptionInfo.getPhoto()).map(photos -> {
            try {
                if (curId == 1) {
                    if (isAdmin()) sendMessCopt(receptionInfo.getPhoto(), receptionInfo.getText(), 1004);
                    if (!isAdmin()) sendMessCopt(receptionInfo.getPhoto(), receptionInfo.getText(), 1007);
                } else {
                    if (isAdmin()) sendMessCopt(receptionInfo.getPhoto(), receptionInfo.getText(), 1003);
                    if (!isAdmin()) sendMessCopt(receptionInfo.getPhoto(), receptionInfo.getText(), 1006);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        }).orElseGet(() -> {

            return Optional.empty();
        });
        return backId = 1;
    }

    private void departmentInfo() throws Exception {
        Optional.ofNullable(departmentsInfo.getPhoto()).map(photo -> {
            try {
                if (isAdmin()) sendMessCopt(departmentsInfo.getPhoto(), departmentsInfo.getText(), 1003);
                if (!isAdmin()) sendMessCopt(departmentsInfo.getPhoto(), departmentsInfo.getText(), 1006);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return photo;
        }).orElseThrow(() -> new Exception("Photo is not found"));
    }

    private void sendMessEmp() {
        receptionEmployees = Optional.ofNullable(receptionEmployeeDao.getReceptionEmployeeById(receptionType.getId())).map(recEmps -> {
            recEmps.forEach(recEmp -> {
                try {
                    User user = userDao.getUserByChatId(chatId);
                    uptMess = sendMessage(String.format(getText(1073), user.getFullName()), recEmp.getChatId());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            });
            return recEmps;
        }).orElse(new ArrayList<>());
    }

    private void sendMessCopt(String photo, String text, int id) throws TelegramApiException {
        uptMess = bot.execute(new SendPhoto().setChatId(chatId).setPhoto(photo).setCaption(text).setReplyMarkup(keyboardMarkUpDao.select(id)).setParseMode(ParseMode.html.name())).getMessageId();
    }

    private void sendMess(int id) throws TelegramApiException {
        uptMess = toDeleteMessage(sendMessage(id));
    }
}