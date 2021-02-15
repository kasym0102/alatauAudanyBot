package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.custom.*;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.entity.standart.Admin;
import com.alatauBot.services.CitizensReportService;
import com.alatauBot.services.SendReportAndMapService;
import com.alatauBot.utils.ButtonsLeaf;
import com.alatauBot.utils.Const;
import com.alatauBot.utils.DateKeyboard;
import com.alatauBot.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
public class id008_Statistics extends Command {
    private int curId, curId1, curId2, uptMess;
    private int level_id, recId, sizeArr;
    private List<AppealType> appealTypes;
    private DateKeyboard dateKeyboard;
    private List<ReceptionType> receptionTypes;
    private List<AppealTeg> appealTegs;
    private List<AppealTask> appealTasks;
    private List<AppealTaskRequestToRenewal> appealTaskRequestToRenewals;
    private ButtonsLeaf buttonsLeaf;
    private String suggestionType;
    private Date start;
    private Date end;
    private final SendReportAndMapService sendReportAndMapService = new SendReportAndMapService();
    private List<String> list;
    private AppealTaskRequestToRenewal appealTaskRequestToRenewal;
    private List<Admin> listOfAdmins; // = adminDao.getAll();
    private List<AppealEmployee> listOfEmployee;
    private StringBuilder stringBuilder = new StringBuilder();

    List<String > answers_of_admin_for_request_responsible = new ArrayList<>();



    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteAll(uptMess);
                list = new ArrayList<>();
                receptionTypeMenu();
                waitingType = WaitingType.SET_TYPE;
                return COMEBACK;
            case SET_TYPE:
                deleteAll(uptMess);
                if (list.get(Integer.parseInt(updateMessageText)).equals(buttonDao.getButtonText(Const.REPORT_BUTTON))) {
                    dateKeyboard = new DateKeyboard();
                    uptMess = sendStartDate();
                    level_id = 1;
                    waitingType = WaitingType.START_DATE;
                }
                else if (list.get(Integer.parseInt(updateMessageText)).equals(buttonDao.getButtonText(Const.LIST_OF_REQUESTS_TO_RENEWAL_DEADLINE))) {
                    level_id = 1;
                    requests_responsible();
                    waitingType = WaitingType.CHOOSE_REQUEST_RESPONSIBLE;
                }
                else {
                    curId1 = Integer.parseInt(updateMessageText);
                    appealTypeMenu();
                    waitingType = WaitingType.SET_TEG;
                }
                return COMEBACK;
            case CHOOSE_REQUEST_RESPONSIBLE:

                choose_request();
                waitingType = WaitingType.REQUEST_RESPONSIBLE_HANDLER;
                return COMEBACK;

            case REQUEST_RESPONSIBLE_HANDLER:

                String buttonName = answers_of_admin_for_request_responsible.get(Integer.parseInt(updateMessageText));


                deleteAll(uptMess);
                String accepted;
                listOfEmployee = appealEmployeeDao.getAll();
                if (buttonName.equals(buttonDao.getButtonText(129))) {
                    appealTaskDao.updateAppealDeadline(appealTaskRequestToRenewal.getDateDeadline(), appealTaskRequestToRenewal.getTaskId());
                    appealTaskRequestToRenewalDao.updateAcceptedTrue(appealTaskRequestToRenewal.getId());
                    accepted = String.format("Ваш запрос № %s \n%s \nОдобрено!", appealTaskRequestToRenewal.getTaskId(), String.valueOf(stringBuilder));

                    int appealTegId = appealTaskDao.getAppealTaskById(appealTaskRequestToRenewal.getTaskId()).getAppealTegId();
                    listOfEmployee = Optional.ofNullable(appealEmployeeDao.getAllByTegId(appealTegId)).orElse(new ArrayList<>());


                    for (AppealEmployee employee : listOfEmployee) {
                        sendMessage(accepted, employee.getChatId());
                    }
                } else if (buttonName.equals(buttonDao.getButtonText(130))) {
                    appealTaskRequestToRenewalDao.updateAcceptedFalse(appealTaskRequestToRenewal.getId());
                    accepted = String.format("Ваш запрос № %s \n%s \nОтклонено!", appealTaskRequestToRenewal.getTaskId(), String.valueOf(stringBuilder));

                    int appealTegId = appealTaskDao.getAppealTaskById(appealTaskRequestToRenewal.getTaskId()).getAppealTegId();

                    listOfEmployee = Optional.ofNullable(appealEmployeeDao.getAllByTegId(appealTegId)).orElse(new ArrayList<>());

                    for (AppealEmployee employee : listOfEmployee) {
                        sendMessage(accepted, employee.getChatId());
                    }


                }
                sendMessage(getText(15));

                return COMEBACK;

            case SET_TEG:
                deleteAll(uptMess);
                if (list.get(Integer.parseInt(updateMessageText)).equals(buttonDao.getButtonText(Const.BACK))) {
                    receptionTypeMenu();
                    waitingType = WaitingType.SET_TYPE;
                } else if (list.get(Integer.parseInt(updateMessageText)).equals(buttonDao.getButtonText(Const.REPORT_BUTTON))) {
                    dateKeyboard = new DateKeyboard();
                    uptMess = sendStartDate();
                    level_id = 2;
                    waitingType = WaitingType.START_DATE;
                } else {
                    list.clear();
                    curId = Integer.parseInt(updateMessageText);
                    appealTegs = Optional.ofNullable(appealTegDao.appealTegList(appealTypes.get(Integer.parseInt(updateMessageText) - 1).getId())).map(appTegs -> {
                        list.add(buttonDao.getButtonText(Const.REPORT_BUTTON));
                        appTegs.forEach(e -> {
                            recId = 0;
                            sizeArr = appealTaskDao.getAllByAppealTegId(e.getId()).size();
                            if (sizeArr == 1) {
                                list.add(e.getName() + hyphen + appealTaskDao.getAppealTaskByAppealTegId(e.getId()).getAppraisalId());
                            } else if (sizeArr > 1) {
                                for (AppealTask appealTask : appealTaskDao.getAllByAppealTegId(e.getId())) {
                                    recId = recId + appealTask.getAppraisalId();
                                }
                                list.add(e.getName() + hyphen + (recId / sizeArr));
                            } else {
                                list.add(e.getName());
                            }
                        });
                        list.add(buttonDao.getButtonText(Const.BACK));
                        buttonsLeaf = new ButtonsLeaf(list);
                        return appTegs;
                    }).orElse(new ArrayList<>());
                    uptMess = toDeleteMessage(sendMessageWithKeyboard(1021, buttonsLeaf.getListButton()));
                    waitingType = WaitingType.SET_REPORT;
                }
                return COMEBACK;
            case START_DATE:
                deleteAll(uptMess);
                if (hasCallbackQuery()) {
                    if (dateKeyboard.isNext(updateMessageText)) {
                        sendStartDate();
                        return COMEBACK;
                    }
                    start = dateKeyboard.getDateDate(updateMessageText);
                    start.setHours(0);
                    start.setMinutes(0);
                    start.setSeconds(0);
                    sendEndDate();
                    waitingType = WaitingType.END_DATE;
                }
                return COMEBACK;
            case END_DATE:
                deleteAll(uptMess);
                if (hasCallbackQuery()) {
                    if (dateKeyboard.isNext(updateMessageText)) {
                        sendStartDate();
                        return COMEBACK;
                    }
                    end = dateKeyboard.getDateDate(updateMessageText);
                    end.setHours(23);
                    end.setMinutes(59);
                    end.setSeconds(59);
                    switch (level_id) {
                        case 1:
                            appealTasks = appealTaskDao.getAllByDate(start, end);
                            sendReportAndMapService.sendReportAndMap(bot, update, appealTasks);
                            break;
                        case 2:
                            appealTasks = appealTaskDao.getAllByReceptionNameDate(receptionTypes.get(curId1 - 1).getId(), start, end);
                            sendReportAndMapService.sendReportAndMap(bot, update, appealTasks);
                            break;
                        case 3:
                            appealTasks = appealTaskDao.getAllByAppealTypeNameDate(receptionTypes.get(curId1 - 1).getId(), appealTypes.get(curId - 1).getId(), start, end);
                            sendReportAndMapService.sendReportAndMap(bot, update, appealTasks);
                            break;
                        case 4:
                            AppealTeg appealTeg = appealTegs.get(curId2);
                            appealTasks = appealTaskDao.getAllByAppealTegNameDate(receptionTypes.get(curId1 - 1).getId(), appealTypes.get(curId - 1).getId(), appealTeg.getId(), start, end);
                            sendReportAndMapService.sendReportAndMap(bot, update, appealTasks);
                            break;
                    }
                    waitingType = WaitingType.END_DATE;
                }
                return COMEBACK;
            case SET_REPORT:
                deleteAll(uptMess);
                if (list.get(Integer.parseInt(updateMessageText)).equals(buttonDao.getButtonText(Const.BACK))) {
                    appealTypeMenu();
                    waitingType = WaitingType.SET_TEG;
                } else if (list.get(Integer.parseInt(updateMessageText)).equals(buttonDao.getButtonText(Const.REPORT_BUTTON))) {
                    dateKeyboard = new DateKeyboard();
                    uptMess = sendStartDate();
                    level_id = 3;
                    waitingType = WaitingType.START_DATE;
                } else {
                    dateKeyboard = new DateKeyboard();
                    uptMess = sendStartDate();
                    curId2 = Integer.parseInt(updateMessageText) - 1;
                    level_id = 4;
                    waitingType = WaitingType.START_DATE;
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private void choose_request() throws TelegramApiException {
        deleteAll(uptMess);

        appealTaskRequestToRenewal = appealTaskRequestToRenewals.get(Integer.parseInt(updateMessageText));
//

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(messageDao.getMessageText(1052)).append(userDao.getUserByChatId(appealTaskRequestToRenewal.getChatId()).getFullName()).append(next);
        stringBuilder.append(messageDao.getMessageText(2004)).append(DateUtil.getDayDate2(appealTaskRequestToRenewal.getDateDeadline())).append(next);
        stringBuilder.append(messageDao.getMessageText(2005)).append(appealTaskRequestToRenewal.getTextCause()).append(next);
        stringBuilder.append(next);
        stringBuilder.append(messageDao.getMessageText(2008)).append(appealTaskDao.getIdById(appealTaskRequestToRenewal.getTaskId()).getId()).append(next);
        stringBuilder.append(messageDao.getMessageText(2003)).append(userDao.getUserByChatId(appealTaskDao.getIdById(appealTaskRequestToRenewal.getTaskId()).getChatId()).getFullName()).append(next);
        stringBuilder.append(messageDao.getMessageText(1053)).append(appealTaskDao.getIdById(appealTaskRequestToRenewal.getTaskId()).getText()).append(next);
        stringBuilder.append(messageDao.getMessageText(1054)).append(appealTaskDao.getIdById(appealTaskRequestToRenewal.getTaskId()).getDataBegin()).append(next);
//                    stringBuilder.append("]").append(next);
        stringBuilder.append(messageDao.getMessageText(2007)).append(appealTaskDao.getIdById(appealTaskRequestToRenewal.getTaskId()).getDataDeadline()).append(next);
//                    stringBuilder.append(messageDao.getMessageText(2004)).append(appealTaskRequestToRenewal.getDateDeadline()).append(next);
//

        //
//        stringBuilder.append(getText(1052)).append(userDao.getUserByChatId(appealTaskRequestToRenewal.getChatId()).getFullName()).append(next);
//        stringBuilder.append(getText(2007)).append(appealTaskDao.getIdById(appealTaskRequestToRenewal.getTaskId()).getDataDeadline()).append(next);
//        stringBuilder.append(getText(2004)).append(appealTaskRequestToRenewal.getDateDeadline()).append(next);
//        stringBuilder.append(getText(1053)).append(appealTaskRequestToRenewal.getTextCause());

        answers_of_admin_for_request_responsible = new ArrayList<>();
        answers_of_admin_for_request_responsible.add(buttonDao.getButtonText(129));
        answers_of_admin_for_request_responsible.add(buttonDao.getButtonText(130));
        ButtonsLeaf bt = new ButtonsLeaf(answers_of_admin_for_request_responsible);

        uptMess = toDeleteKeyboard(sendMessageWithKeyboard(stringBuilder.toString(), bt.getListButton()));
        try {
            sendMessage(String.format(getText(2011), appealTaskRequestToRenewal.getTaskId()));

            if (appealTaskDao.getIdById(appealTaskRequestToRenewal.getTaskId()).getPhoto() != null)
                bot.execute(new SendPhoto().setChatId(chatId).setPhoto(appealTaskDao.getIdById(appealTaskRequestToRenewal.getTaskId()).getPhoto())).getMessageId();
            if (appealTaskDao.getIdById(appealTaskRequestToRenewal.getTaskId()).getVideo() != null)
                bot.execute(new SendVideo().setChatId(chatId).setVideo(appealTaskDao.getIdById(appealTaskRequestToRenewal.getTaskId()).getVideo())).getMessageId();
            SendLocation sendLocation = new SendLocation();
            sendLocation.setLatitude(Float.parseFloat(appealTaskDao.getIdById(appealTaskRequestToRenewal.getTaskId()).getLocation().split(Const.HESH)[0]));
            sendLocation.setLongitude(Float.parseFloat(appealTaskDao.getIdById(appealTaskRequestToRenewal.getTaskId()).getLocation().split(Const.HESH)[1]));
            bot.execute(sendLocation.setChatId(chatId)).getMessageId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

//        uptMess = toDeleteKeyboard(sendMessageWithKeyboard(stringBuilder.toString(), 1008));
    }

    private void appealTypeMenu() throws TelegramApiException {
        list.clear();
        appealTypes = Optional.ofNullable(appealTypeDao.getAppealTypeByReceptionType(receptionTypes.get(Integer.parseInt(updateMessageText) - 1).getId())).map(appTypes -> {
            list.add(buttonDao.getButtonText(Const.REPORT_BUTTON));
            appTypes.forEach(appType -> list.add(appType.getName()));
            list.add(buttonDao.getButtonText(Const.BACK));
            buttonsLeaf = new ButtonsLeaf(list);
            return appTypes;
        }).orElse(new ArrayList<>());
        uptMess = toDeleteMessage(sendMessageWithKeyboard(1021, buttonsLeaf.getListButton()));
    }


    private void receptionTypeMenu() throws TelegramApiException {
        list.clear();
        receptionTypes = Optional.ofNullable(receptionTypeDao.getAllByAppealTypeId(2)).map(recTypes -> {
            list.add(buttonDao.getButtonText(Const.LIST_OF_REQUESTS_TO_RENEWAL_DEADLINE));
            list.add(buttonDao.getButtonText(Const.REPORT_BUTTON));
            recTypes.forEach(recType -> list.add(recType.getName()));
            buttonsLeaf = new ButtonsLeaf(list);
            return recTypes;
        }).orElse(new ArrayList<>());
        uptMess = toDeleteKeyboard(sendMessageWithKeyboard(Const.CHOOSE_CATEGORY, buttonsLeaf.getListButton()));
    }

    private void requests_responsible() throws TelegramApiException {
        list.clear();
        appealTaskRequestToRenewals = Optional.ofNullable(appealTaskRequestToRenewalDao.getAll()).map(recTypes -> {
            recTypes.forEach(recType -> list.add("№ " + recType.getTaskId()));
            buttonsLeaf = new ButtonsLeaf(list);
            return recTypes;
        }).orElse(new ArrayList<>());
        uptMess = toDeleteKeyboard(sendMessageWithKeyboard(2002, buttonsLeaf.getListButton()));
    }


    private int sendStartDate() throws TelegramApiException {
        return toDeleteKeyboard(sendMessageWithKeyboard(1065, dateKeyboard.getCalendarKeyboard()));
    }

    private int sendEndDate() throws TelegramApiException {
        return toDeleteKeyboard(sendMessageWithKeyboard(1066, dateKeyboard.getCalendarKeyboard()));
    }

    private void sendReport() throws TelegramApiException {
        int preview = sendMessage("Отчет подготавливается...");
        CitizensReportService reportService = new CitizensReportService();
        reportService.sendCitizenReport(chatId, bot, start, end, suggestionType, preview);
    }
}