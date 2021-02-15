package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.dao.impl.DirectorsDepartmentsDao;
import com.alatauBot.entity.custom.*;
import com.alatauBot.entity.enums.Language;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.entity.standart.Admin;
import com.alatauBot.entity.standart.User;
import com.alatauBot.utils.ButtonsLeaf;
import com.alatauBot.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class id010_EditCategory extends Command {
    private List<ReceptionType> receptionTypes;
    private List<ReceptionEmployee> receptionEmployees;
    private List<AppealTeg> appealTegs;
    private List<AppealType> appealTypes;
    private List<User> users;
    private List<String> list, list1;
    private AppealTeg appealTeg;
    private AppealType appealType;
    private ReceptionEmployee receptionEmployee;
    private ReceptionType receptionType;
    private ButtonsLeaf buttonsLeaf;
    private StringBuilder sb;
    private String exText;
    private int curId, getCurIdOne, getGetCurIdTwe;
    private int messId, i;
    private int curLangId = Language.ru.getId();
    private String icon = "🇷🇺";
    private int delMesId;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }

        switch (waitingType) {
            case START:
                deleteAll(messId);
                list = new ArrayList<>();
                list1 = new ArrayList<>();
                if (isButton(34)) { //
                    menuCat();
                    waitingType = WaitingType.EDIT_CATEGORY;
                }
                if (isButton(35)) { // обращения
                    appealTypes = appealTypeDao.getAppealTypes();
                    menuCat2();
                    waitingType = WaitingType.EDIT_CATEGORY2;
                }
                return COMEBACK;
            case EDIT_CATEGORY2:
                deleteAll(messId);
                if (updateMessageText.equals(getButtonText(swap))) { // TODO: PRESS BUTTON SWAP LANGUAGE
                    if (curLangId == Language.ru.getId()) {
                        curLangId = Language.kz.getId();
                        icon = "\uD83C\uDDF0\uD83C\uDDFF";
                    } else {
                        curLangId = Language.ru.getId();
                        icon = "🇷🇺";
                    }
                    menuCat2();
                    waitingType = WaitingType.EDIT_CATEGORY2;
                }
                else if (list.contains(updateMessageText)) { // TODO: PRESS BUTTON EDITION
                    curId = onlyNumbers(updateMessageText) - 1;
                    appealType = appealTypeDao.getAppealType(appealTypes.get(curId).getId());
                    messId = sendMessage(getText(Const.ENTRY_NEW_NAME_CATEGORY));
                    waitingType = WaitingType.EDIT_CATEGORY_NAME2;
                }
                else if (list1.contains(updateMessageText)) { // TODO: PRESS BUTTON OPTION

                    curId = onlyNumbers(updateMessageText) - 1;
                    getGetCurIdTwe = curId;

                    menuTegs(getGetCurIdTwe);
                    waitingType = WaitingType.DELETE_EMPLOYEE2;
                }

                return COMEBACK;
            case EDIT_CATEGORY:
                deleteAll(messId);
                if (updateMessageText.equals(getButtonText(swap))) { // TODO: PRESS BUTTON SWAP LANGUAGE
                    if (curLangId == Language.ru.getId()) {
                        curLangId = Language.kz.getId();
                        icon = "\uD83C\uDDF0\uD83C\uDDFF";
                    } else {
                        curLangId = Language.ru.getId();
                        icon = "🇷🇺";
                    }
                    menuCat();
                    waitingType = WaitingType.EDIT_CATEGORY;
                }
                if (list.contains(updateMessageText)) {
                    curId = onlyNumbers(updateMessageText) - 1;
                    receptionType = receptionTypeDao.getById(receptionTypes.get(curId).getId());
                    messId = toDeleteKeyboard(sendMessage(Const.ENTRY_NEW_NAME_CATEGORY));
                    waitingType = WaitingType.EDIT_CATEGORY_NAME;
                } else if (list1.contains(updateMessageText)) {
                    curId = onlyNumbers(updateMessageText) - 1;
                    getCurIdOne = curId;
                    menuEmployees(curId);
                    waitingType = WaitingType.DELETE_EMPLOYEE;
                }
                return COMEBACK;
            case EDIT_CATEGORY_NAME2:
                deleteAll(messId);
                if (hasMessageText()) {
                    appealType.setLanguageId(curLangId);
                    appealType.setName(updateMessageText);
                    appealTypeDao.update(appealType);
                    menuCat2();
                    waitingType = WaitingType.EDIT_CATEGORY2;
                }
                return COMEBACK;
            case EDIT_CATEGORY_NAME:
                deleteAll(messId);
                if (hasMessageText()) {
                    receptionType.setLanguageId(curLangId);
                    receptionType.setName(updateMessageText);
                    receptionTypeDao.update(receptionType);
                    menuCat();
                    waitingType = WaitingType.EDIT_CATEGORY;
                    return COMEBACK;
                }
            case DELETE_EMPLOYEE:
                deleteAll(messId);
                if (updateMessageText.equals(getButtonText(snew))) {
                    list.clear();
                    i = 0;
                    sb = new StringBuilder();
                    users = Optional.ofNullable(userDao.getAll()).map(users -> {
                        users.forEach(user -> {
                            sb.append(Const.ADD + (++i)).append(" - ").append(user.getFullName()).append(next);
                            list.add(Const.ADD + (i));
                        });
                        return users;
                    }).orElse(new ArrayList<>());
                    messId = sendMessGetMessId(String.format(getText(1015), sb.toString()));
                    waitingType = WaitingType.ADD_EMPLOYEE;
                    return COMEBACK;
                }
                if (list.contains(updateMessageText)) {
                    curId = onlyNumbers(updateMessageText) - 1;
                    receptionEmployeeDao.delete(receptionEmployees.get(curId).getId());
                    menuEmployees(getCurIdOne);
                    waitingType = WaitingType.DELETE_EMPLOYEE;
                    return COMEBACK;
                }
            case DELETE_EMPLOYEE2:
                deleteAll(messId);
                if (updateMessageText.equals(getButtonText(swap))) { // TODO: PRESS BUTTON SWAP LANGUAGE
                    if (curLangId == Language.ru.getId()) {
                        curLangId = Language.kz.getId();
                        icon = "\uD83C\uDDF0\uD83C\uDDFF";
                    } else {
                        curLangId = Language.ru.getId();
                        icon = "🇷🇺";
                    }
                    menuTegs(getGetCurIdTwe);
                    waitingType = WaitingType.DELETE_EMPLOYEE2;
                    return COMEBACK;
                }
                if (updateMessageText.equals(getButtonText(sback))) {
                    appealTypes = appealTypeDao.getAppealTypes();
                    menuCat2();
                    waitingType = WaitingType.EDIT_CATEGORY2;
                    return COMEBACK;
                }
                if (updateMessageText.equals(getButtonText(snew))) { // TODO: PRESS BUTTON NEW nachalnik
                    list.clear();
//                    appealTeg = new AppealTeg();
//                    sb = new StringBuilder();
                    i = 0;
                    if (updateMessageText.equals("/new")){
                        delMesId =  toDeleteMessage(sendMessage(1011));
                        waitingType = WaitingType.ADD_DIRECTOR;
                        return COMEBACK;
                    }
                    // todo : отправте контакт
//                    appealTegs = Optional.ofNullable(appealTegDao.getAll()).map(appTegs -> {
//                        appTegs.forEach(appTeg -> {
//                            sb.append(Const.ADD + (++i)).append(hyphen).append(appTeg.getName()).append(next);
//                            list.add(Const.ADD + i);
//                        });
//                        return appTegs;
//                    }).orElse(new ArrayList<>());



//                    toDeleteKeyboard(sendMessage(String.format(getText(1071), sb.toString())));
//                    waitingType = WaitingType.ADD_TEG;

                    return COMEBACK;
                }
                if (list.contains(updateMessageText)) { // TODO: PRESS BUTTON DELETE nacalnik
                    curId = onlyNumbers(updateMessageText);

//                    appealTeg = appealTegDao.getByIdAppeal(appealTegs.get(curId).getId(), appealType.getId());
//                    appealTegDao.delete(appealTeg.getId());

                    directorsDepartmentsDao.delete(curId);

                    menuTegs(getGetCurIdTwe);
                    waitingType = WaitingType.DELETE_EMPLOYEE2;
                    return COMEBACK;
                }
                if (updateMessageText.equals(getButtonText(sback))) {
                    appealTypes = appealTypeDao.getAppealTypes();
                    menuCat2();
                    waitingType = WaitingType.EDIT_CATEGORY2;
                    return COMEBACK;
                }

            case ADD_DIRECTOR:
                deleteAll(delMesId);
                if (update.getMessage().hasContact()){
//                    admin = new Admin();
                    Director_departments director = new Director_departments();

                    director.setChatId(update.getMessage().getContact().getUserID().intValue());

//                    director.setAppealType_id(getGetCurIdTwe + 2);
                    director.setAppealType_id(appealTypes.get(getGetCurIdTwe).getId());

//                    admin.setUserId(update.getMessage().getContact().getUserID().longValue());
//                    admin.setComment(update.getMessage().getContact().getFirstName() + " " + update.getMessage().getContact().getLastName());
                    directorsDepartmentsDao.insert(director);
//                    adminDao.insertAdmin(admin);
                    menuTegs(getGetCurIdTwe);

//                    menuAdminInfo();
                    waitingType = WaitingType.DELETE_EMPLOYEE2;
                }
                return COMEBACK;
            case ADD_TEG:
                deleteAll(messId);
                if (list.contains(updateMessageText)) {
                    curId = onlyNumbers(updateMessageText) - 1;
                    appealTeg.setAppealTypeId(appealType.getId());
                    appealTeg.setId(appealTegs.get(curId).getId());
                    appealTeg.setLang_id(appealTegs.get(curId).getLang_id());
                    appealTegDao.updateAll(appealTeg);
                    menuTegs(getGetCurIdTwe);
                    waitingType = WaitingType.DELETE_EMPLOYEE2;
                    return COMEBACK;
                }
            case ADD_EMPLOYEE:
                deleteAll(messId);
                if (list.contains(updateMessageText)) {
                    receptionEmployee = new ReceptionEmployee();
                    curId = onlyNumbers(updateMessageText) - 1;
                    receptionEmployee.setReceptionId(receptionType.getId());
                    receptionEmployee.setName(users.get(curId).getFullName());
                    receptionEmployee.setChatId(users.get(curId).getChatId());
                    receptionEmployeeDao.insert(receptionEmployee);
                    menuEmployees(getCurIdOne);
                    waitingType = WaitingType.DELETE_EMPLOYEE;
                    return COMEBACK;
                }
        }
        return EXIT;
    }

    private void menuCat() throws TelegramApiException {
        receptionTypes = new ArrayList<>();
        receptionType = new ReceptionType();
        i = 0;
        list.clear();
        list1.clear();
        sb = new StringBuilder();
        if (curLangId == Language.kz.getId()) {
            receptionType.setLanguageId(2);
        } else {
            receptionType.setLanguageId(1);
        }
        receptionTypes = Optional.ofNullable(receptionTypeDao.getAllKz(receptionType)).map(recTypes -> {
            recTypes.forEach(recType -> {
                sb.append(String.format(getText(1017), Const.EDIT + (++i), Const.OPT + (i), recType.getName())).append(next);
                list.add(Const.EDIT + i);
                list1.add(Const.OPT + i);
            });
            return recTypes;
        }).orElse(new ArrayList<>());
        messId = toDeleteMessage(sendMessage(String.format(getText(1012), buttonDao.getButtonText(34), icon, sb.toString())));
    }

    private void menuCat2() throws TelegramApiException {
        appealType = new AppealType();
        list.clear();
        list1.clear();
        sb = new StringBuilder();
        i = 0;
        if (curLangId == Language.kz.getId()){
            appealType.setLanguageId(2);
        }else {
            appealType.setLanguageId(1);
        }
        appealTypes = Optional.ofNullable(appealTypeDao.getAppealTypesKz(appealType)).map(appTypes -> {
            appTypes.forEach(appType -> {
                sb.append(String.format(getText(1017), Const.EDIT + (++i), Const.OPT + (i), appType.getName())).append(next);
                list.add(Const.EDIT + i);
                list1.add(Const.OPT + i);
            });
            return appTypes;
        }).orElse(new ArrayList<>());
        messId = toDeleteMessage(sendMessage(String.format(getText(1012), buttonDao.getButtonText(35), icon, sb.toString())));
    }

    private void menuEmployees(int id) throws TelegramApiException {
        receptionType = receptionTypes.get(id);
        sb = new StringBuilder();
        i = 0;
        list.clear();
        receptionEmployees = Optional.ofNullable(receptionEmployeeDao.getReceptionEmployeeById(receptionTypes.get(id).getId())).map(receEmps -> {
            receEmps.forEach(receEmp -> {
                sb.append(getButtonText(sdell) + (++i)).append(hyphen).append(receEmp.getName()).append(next);
                list.add(getButtonText(sdell) + i);
            });
            return receEmps;
        }).orElse(new ArrayList<>());
        messId = toDeleteMessage(sendMessage(String.format(getText(1013), sb.toString())));
    }

    private void menuTegs(int id) throws TelegramApiException {

        List<Director_departments> director_departments = new ArrayList<>();

        appealTeg = new AppealTeg();
        sb = new StringBuilder();
        i = 0;
        list.clear();

        appealTeg.setLang_id(curLangId);
        appealTeg.setAppealTypeId(appealTypes.get(id).getId());

        appealType = appealTypes.get(id);

        director_departments = directorsDepartmentsDao.getAllByAppealTypeId(appealType.getId());

//        appealTegs = Optional.ofNullable(appealTegDao.appealTegList(appealTeg)).map(appTegs -> {
//            appTegs.forEach(appTeg -> {
//                sb.append(getButtonText(sdell) + (++i) + plus).append(hyphen).append("\uD83D\uDD0E" + appTeg.getName()).append(next);
//                list.add(getButtonText(sdell) + i);
//            });
//            return appTegs;
//        }).orElse(new ArrayList<>());

        Optional.ofNullable(director_departments).map(directors->{
            directors.forEach(director->{
                sb.append(getButtonText(sdell) + (director.getId()) + plus).append(hyphen).append("\uD83D\uDD0E" + userDao.getFullNameByChatId(director.getChatId())).append(next);
                list.add(getButtonText(sdell) + director.getId());
            });
            return directors;
        }).orElse(new ArrayList<>());

        messId = toDeleteMessage(sendMessage(String.format(getText(51), icon, sb.toString())));
    }
}
