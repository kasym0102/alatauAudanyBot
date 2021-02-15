package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.custom.AppealEmployee;
import com.alatauBot.entity.custom.AppealTeg;
import com.alatauBot.entity.custom.AppealType;
import com.alatauBot.entity.custom.ReceptionType;
import com.alatauBot.entity.enums.Language;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.utils.ButtonsLeaf;
import com.alatauBot.utils.Const;
import org.springframework.dao.EmptyResultDataAccessException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class id011_EditTeg extends Command {
    private List<AppealTeg> appealTegs;
    private List<AppealType> appealTypes;
    private List<ReceptionType> receptionTypes;
    private List<AppealEmployee> appealEmployees;
    private AppealEmployee appealEmployee;
    private AppealTeg appealTeg;
    private int curId;
    private int i;
    private StringBuilder sb1, sb2;
    private int levelId;
    private int curLangId = Language.ru.getId();
    private String icon = "🇷🇺";
    private List<String> list = new ArrayList<>(),
            list1 = new ArrayList<>(),
            list2 = new ArrayList<>(),
            list3 = new ArrayList<>(),
            list4 = new ArrayList<>();

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                menuAllTeg(icon);
                waitingType = WaitingType.EDIT_TEG;
                return COMEBACK;
            case EDIT_TEG:
                delete();
                if (updateMessageText.equals(getButtonText(swap))) {
                    if (curLangId == Language.ru.getId()) {
                        curLangId = Language.kz.getId();
                        icon = "\uD83C\uDDF0\uD83C\uDDFF";
                    } else {
                        curLangId = Language.ru.getId();
                        icon = "🇷🇺";
                    }
                    menuAllTeg(icon);
                    waitingType = WaitingType.EDIT_TEG;
                    return COMEBACK;
                } else if (updateMessageText.equals(getButtonText(snew))) {
                    list.clear();
                    appealTeg = new AppealTeg();
                    appealTeg.setId(appealTegDao.getMaxId() + 1);
                    appealTeg.setLang_id(curLangId);
                    receptionTypes = receptionTypeDao.getAllByAppealTypeId(2);
                    receptionTypes.forEach(e -> list.add(e.getName()));
                    ButtonsLeaf bl = new ButtonsLeaf(list);
                    levelId = 33;
                    sendMessageWithKeyboard(getText(1020), bl.getListButton());
                    waitingType = WaitingType.PRESS_COMMAND;
                    return COMEBACK;
                } else {
                    curId = onlyNumbers(updateMessageText) - 1;
                    if (list1.contains(updateMessageText)) {
                        menuOneTeg(curId);
                        waitingType = WaitingType.PRESS_COMMAND;
                        return COMEBACK;
                    } else if (list2.contains(updateMessageText)) {
                        appealTeg = appealTegDao.getOneById(appealTegs.get(curId).getId(), curLangId);
                        try {
                            menuAllEmployeeTeg();
                        } catch (EmptyResultDataAccessException e) {
                            sendMessage(1067);
                        }
                        levelId = 6;
                        waitingType = WaitingType.PRESS_COMMAND;
                        return COMEBACK;
                    }
                }
            case PRESS_COMMAND:
                delete();
                if (levelId == 1) {
                    if (updateMessageText.equals(getButtonText(swap))) {
                        if (curLangId == Language.ru.getId()) {
                            curLangId = Language.kz.getId();
                            icon = "\uD83C\uDDF0\uD83C\uDDFF";
                        } else {
                            curLangId = Language.ru.getId();
                            icon = "🇷🇺";
                        }
                        menuOneTeg(curId);
                        waitingType = WaitingType.PRESS_COMMAND;
                        return COMEBACK;
                    }
//                    else if (updateMessageText.equals(Const.EDIT_DESC)) {
//                        sendMessage(1081);
//                        levelId = 8;
//                        return COMEBACK;
//                    }
                    else if (updateMessageText.equals(Const.EDIT)) {
                        sendMessage(Const.UPDATE_TEG_NAME);
                        levelId = 2;
                        return COMEBACK;
                    } else if (updateMessageText.equals(Const.DELL)) {
                        appealTegDao.delete(appealTeg.getId());
                        menuAllTeg(icon);
                        waitingType = WaitingType.EDIT_TEG;
                        return COMEBACK;
                    }
//                    else if (updateMessageText.equals(Const.BAC)) {
//                        menuAllTeg(icon);
//                        waitingType = WaitingType.EDIT_TEG;
//                        return COMEBACK;
//                    }
                } else if (levelId == 2) {
                    if (update.getMessage().hasText()) {
                        appealTeg.setName(updateMessageText);
                        appealTeg.setLang_id(curLangId);
                        appealTegDao.update(appealTeg);
                        menuOneTeg(curId);
                        waitingType = WaitingType.PRESS_COMMAND;
                        return COMEBACK;
                    }
                } else if (levelId == 33) {
                    if (update.hasCallbackQuery()) {
                        list4.clear();
                        appealTeg.setReceptionTypeId(receptionTypes.get(Integer.parseInt(updateMessageText)).getId());
                        appealTypes = appealTypeDao.getAppealTypesByReceptionTypeId(receptionTypes.get(Integer.parseInt(updateMessageText)).getId());
                        appealTypes.forEach(e -> list4.add(e.getName()));
                        ButtonsLeaf bl33 = new ButtonsLeaf(list4);
                        levelId = 3;
                        sendMessageWithKeyboard(getText(Const.CHOOSE_CATEGORY), bl33.getListButton());
                        return COMEBACK;
                    }
                } else if (levelId == 3) {
                    if (update.hasCallbackQuery()) {
                        appealTeg.setAppealTypeId(appealTypes.get(Integer.parseInt(updateMessageText)).getId());
                        sendMessage(Const.ENTER_TEG_NAME_RU);
                        levelId = 4;
                        return COMEBACK;
                    }
                } else if (levelId == 4) {
                    if (update.getMessage().hasText()) {
                        appealTeg.setName(updateMessageText);
                        appealTegDao.insertKz(appealTeg);
                        sendMessage(Const.ENTER_TEG_NAME_KZ);
                        levelId = 5;
                        return COMEBACK;
                    }
                } else if (levelId == 5) {
                    if (update.getMessage().hasText()) {
                        appealTeg.setName(updateMessageText);
                        appealTeg.setLang_id(2);
                        appealTegDao.insertKz(appealTeg);
                        menuAllTeg(icon);
                        waitingType = WaitingType.EDIT_TEG;
                        return COMEBACK;
                    }
                } else if (levelId == 6) {
                    if (list3.contains(updateMessageText)) {
                        appealEmployeeDao.delete(appealEmployees.get(onlyNumbers(updateMessageText) - 1).getId());
                        menuAllEmployeeTeg();
                        return COMEBACK;
                    }
                    if (update.getMessage().hasContact()) {
                        appealEmployee = new AppealEmployee();
                        appealEmployee.setTegId(appealTeg.getId());
                        appealEmployee.setChatId(update.getMessage().getContact().getUserID());
                        appealEmployee.setName(update.getMessage().getContact().getFirstName());
                        appealEmployeeDao.insert(appealEmployee);
                        menuAllEmployeeTeg();
                        return COMEBACK;
                    }
                } else if (levelId == 7) {
                    if (update.getMessage().hasContact()) {
                        menuAllTeg(icon);
                        waitingType = WaitingType.EDIT_TEG;
                        return COMEBACK;
                    }
                } else if (levelId == 8) {
                    if (update.getMessage().hasText()) {
                        appealTegDao.updateDescription(appealTeg.getId(), Language.ru.getId(), updateMessageText);
                        sendMessage(1082);
                        levelId = 9;
                        return COMEBACK;
                    }
                } else if (levelId == 9){
                    if (update.getMessage().hasText()){
                        appealTegDao.updateDescription(appealTeg.getId(), Language.kz.getId(), updateMessageText);
//                        sendMessage(1083);
                        menuOneTeg(curId);
                        waitingType = WaitingType.PRESS_COMMAND;
                        return COMEBACK;
                    }
                }
        }
        return EXIT;
    }

    private List<AppealEmployee> menuAllEmployeeTeg() throws TelegramApiException {
        list3.clear();
        i = 0;
        sb2 = new StringBuilder();
        appealEmployees = Optional.ofNullable(appealEmployeeDao.getOneById(appealTeg.getId())).map(appealEmployees1 -> {
            appealEmployees1.forEach(appealEmployee1 -> {
                sb2.append(Const.DELL).append(++i).append(hyphen).append(appealEmployee1.getName()).append(next);
                list3.add(Const.DELL + i);
            });
            return appealEmployees1;
        }).orElse(new ArrayList<>());
        sendMessage(String.format(getText(1029), getText(1030) + next, sb2.toString(), getText(1067)));
        return appealEmployees;
    }

    public int menuOneTeg(int curId) throws TelegramApiException {
        appealTeg = Optional.ofNullable(appealTegDao.getOneById(appealTegs.get(curId).getId(), curLangId)).orElse(new AppealTeg());
//        if (appealTeg.getDesc() != null)
//            sendMessage(String.format(getText(1028), getText(1048), icon, appealTeg.getName(), appealTeg.getDesc(), Const.EDIT,Const.DELL, Const.BAC));
//        if (appealTeg.getDesc() == null)
            sendMessage(String.format(getText(1028), getText(1048), icon, appealTeg.getName(), space, Const.EDIT, Const.DELL, Const.BAC));
        return levelId = 1;
    }

    public void menuAllTeg(String icon) throws TelegramApiException {
        sb1 = new StringBuilder();
        i = 0;
        list1.clear();
        list2.clear();
        appealTegs = appealTegDao.getAll(curLangId);
        appealTegs.forEach(e -> {
            ++i;
            list1.add(Const.EDIT + i);
            list2.add(Const.EMP + i);
            sb1.append(String.format(getText(1017), Const.EDIT + i, Const.EMP + i, e.getName())).append(next);
        });
        sendMessage(String.format(getText(1016), icon, sb1.toString()));
    }

    public void delete() {
        deleteMessage(updateMessageId - 1);
        deleteMessage(updateMessageId);
    }
}