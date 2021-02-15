package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.custom.*;
import com.alatauBot.entity.enums.FileType;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.entity.standart.Admin;
import com.alatauBot.entity.standart.User;
import com.alatauBot.utils.Const;
import com.alatauBot.utils.DateKeyboard;
import com.alatauBot.utils.DateUtil;
import com.alatauBot.utils.components.IKeyboardOld;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class id020_SendMessInReception extends Command {
    private AppealTask appealTask;
    private String text, name, statusName;
    private String nameEmployee;
    private String accepted;
    private User user;
    private int statusId;
    private int uptMess;
    private long saveChatId;
    private List<AppealEmployee> allAppealEmployee;
    private int taskId;
    private int taskIdForRenewalDeadline;
//    private long empChatId;
    private int appealTypeId;
    private AppealTaskArchive appealTaskArchive;
    private AppealTaskRequestToRenewal appealTaskRequestToRenewal = new AppealTaskRequestToRenewal();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DateKeyboard dateKeyboard;
    private Date newDeadlineDate;
    private List<Admin> listOfAdmins = adminDao.getAll();


    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        switch (waitingType) {
            case START:
                deleteAll(uptMess);
                if (update.hasCallbackQuery()) {
                    text = update.getCallbackQuery().getMessage().getText();
                    taskId = onlyNumbers(text.split(next)[0]);
                    appealTask = appealTaskDao.getIdById(taskId);
                    if (isButton(57)) {
                        appealTaskDao.updateAppraisalId(5, taskId);
                        uptMess = sendMessage(1064);
                        return EXIT;
                    }
                    if (isButton(58)) {
                        appealTaskDao.updateAppraisalId(3, taskId);
                        uptMess = sendMessage(1064);
                        return EXIT;
                    }
                    if (isButton(59)) {
                        appealTaskDao.updateAppraisalId(1, taskId);
                        uptMess = sendMessage(1064);
                        return EXIT;
                    }
                    if (isButton(Const.DONE)) { // TODO: PRESS TO BUTTON DONE
                        statusId = 1;
                        appealTaskArchive = new AppealTaskArchive();
                        name = text.split(next)[1].split(":")[1];
//                        empChatId = receptionTypeDao.getChatIdById(appealTask.getReceptionTypeId());
                        appealTypeId = appealTask.getAppealTypeId();
                        uptMess = sendMessage(1056);
                        waitingType = WaitingType.SET_TEXT;
                    }
//                    if (isButton(Const.NOT_EXECUTED)) { // TODO: PRESS TO BUTTON NOT EXECUTED
//                        statusId = 2;
//                        appealTaskArchive = new AppealTaskArchive();
//                        name = text.split(next)[1].split(":")[1];
//                        empChatId = receptionTypeDao.getChatIdById(appealTask.getReceptionTypeId());
//                        uptMess = sendMessage(1056);
//                        appealTaskDao.updateStatus(2, taskId);
//                        waitingType = WaitingType.SET_TEXT;
//                    }
                    if (isButton(Const.APPROVED)) { // TODO: PRESS TO BUTTON APPROVED // кнопка одобрено
                        user = userDao.getUserById(appealTask.getFullName());
                        appealTaskArchive = appealTaskArchiveDao.getOneByTaskId(taskId);
                        sendMessageInReceptionType(appealTaskArchive, user.getChatId(), 202);
                    }
                    if (isButton(Const.NOT_APPROVED)) { // TODO: PRESS TO BUTTON NOT APPROVED
                        deleteAll(uptMess);
                        uptMess = sendMessage(1056);
                        waitingType = WaitingType.SET_TEXT_DESC;
                        return COMEBACK;
                    }
                    if (isButton(128)) { // TODO: PRESS TO BUTTON "Запросить продление срока"

                        if (appealTaskRequestToRenewalDao.isExistByTaskId(appealTask.getId())){
                            deleteAll(uptMess);
                            uptMess = sendMessage(2009);
                            waitingType = WaitingType.START;
                            return COMEBACK;
                        }

                        taskIdForRenewalDeadline = onlyNumbers(text.split(next)[0]);
                        deleteAll(uptMess);
                        uptMess = sendMessage(1056);
                        waitingType = WaitingType.SET_TEXT_CAUSE_TO_RENEWAL;
//                        return COMEBACK;
                    }
                }
                return COMEBACK;
            case SET_TEXT_DESC:
                if (hasMessageText()) {
                    deleteAll(uptMess);
                    StringBuilder employeeName = new StringBuilder();
                    allAppealEmployee = appealEmployeeDao.getAllByTegId(appealTegDao.getAppealTegById(appealTask.getAppealTegId()).getId());

                    allAppealEmployee.forEach(appealEmployee1 -> employeeName.append(userDao.getUserByChatId(appealEmployee1.getChatId()).getFullName()).append(next));
                    user = userDao.getUserById(appealTask.getFullName());
                    uptMess = sendMessage(1059);

                    appealTaskDao.updateStatus(3, appealTask.getId());

                    sendMessageToEmployee(user.getFullName(), appealTask.getDataBegin(), appealTask.getText(), appealTask.getId(), employeeName);
                    return EXIT;
                }
                return COMEBACK;
            case SET_TEXT_CAUSE_TO_RENEWAL:
                deleteAll(uptMess);
                if (hasMessageText()) {
                    user = userDao.getUserById(appealTask.getFullName());
                    appealTaskRequestToRenewal.setTaskId(taskIdForRenewalDeadline);
                    appealTaskRequestToRenewal.setTextCause(updateMessageText);
                    appealTaskRequestToRenewal.setChatId(Long.valueOf(update.getMessage().getFrom().getId()));
                    appealTaskRequestToRenewal.setOldDateDeadline(appealTask.getDataDeadline());
//                    uptMess = sendMessage(2001);
                    dateKeyboard = new DateKeyboard();
                    uptMess = sendStartDate();
                    waitingType = WaitingType.SET_RENEWAL_DEADLINE;
                }
                return COMEBACK;
            case SET_RENEWAL_DEADLINE:
                deleteAll(uptMess);
                if (hasCallbackQuery()) {
                    if (dateKeyboard.isNext(updateMessageText)) {
                        sendStartDate();
                        return COMEBACK;
                    }

                    newDeadlineDate = dateKeyboard.getDateDate(updateMessageText);

                    if (newDeadlineDate.before(appealTask.getDataDeadline())){
                        deleteAll(uptMess);
                        dateKeyboard = new DateKeyboard();
                        toDeleteKeyboard(sendMessageWithKeyboard(2010, dateKeyboard.getCalendarKeyboard()));
                        waitingType = WaitingType.SET_RENEWAL_DEADLINE;
                        return COMEBACK;
                    }

                    appealTaskRequestToRenewal.setDateDeadline(newDeadlineDate);
                    appealTaskRequestToRenewalDao.insertAppealTask(appealTaskRequestToRenewal);
//                    AppealTask
                    appealTask = appealTaskDao.getIdById(taskId);
//                    AppealEmployee appealEmployee = appealEmployeeDao.getOneByChatId(appealTask.getChatId());
                    // рассылка сообщений админам

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(messageDao.getMessageText(1052)).append(userDao.getUserByChatId(appealTaskRequestToRenewal.getChatId()).getFullName()).append(next);
                    stringBuilder.append(messageDao.getMessageText(2004)).append(DateUtil.getDayDate2(appealTaskRequestToRenewal.getDateDeadline())).append(next);
                    stringBuilder.append(messageDao.getMessageText(2005)).append(appealTaskRequestToRenewal.getTextCause()).append(next);
                    stringBuilder.append(next);
                    stringBuilder.append(messageDao.getMessageText(2008)).append(appealTask.getId()).append(next);
                    stringBuilder.append(messageDao.getMessageText(2003)).append(userDao.getUserByChatId(appealTask.getChatId()).getFullName()).append(next);
                    stringBuilder.append(messageDao.getMessageText(1053)).append(appealTask.getText()).append(next);
                    stringBuilder.append(messageDao.getMessageText(1054)).append(appealTask.getDataBegin()).append(next);
//                    stringBuilder.append("]").append(next);
                    stringBuilder.append(messageDao.getMessageText(2007)).append(appealTaskDao.getIdById(appealTaskRequestToRenewal.getTaskId()).getDataDeadline()).append(next);
//                    stringBuilder.append(messageDao.getMessageText(2004)).append(appealTaskRequestToRenewal.getDateDeadline()).append(next);
                    accepted = String.format(messageDao.getMessageText(2006), appealTaskRequestToRenewal.getTaskId(), String.valueOf(stringBuilder));


                    sendMessageToAdmins(accepted);
                    //

                    sendMessageWithKeyboard(messageDao.getMessage(1059).getName(), 102);

                    return COMEBACK;
                }
                return COMEBACK;


            case SET_TEXT:
                deleteAll(uptMess);
                if (hasMessageText()) {
                    appealTaskArchive.setTaskId(taskId);
                    appealTaskArchive.setText(updateMessageText);
                    uptMess = sendMessage(1057);
                    waitingType = WaitingType.SET_PHOTO_OR_VIDEO;
                }
                return COMEBACK;
            case SET_PHOTO_OR_VIDEO:
                deleteAll(uptMess);
//                nameEmployee = userDao.getFullNameByChatId(update.getMessage().getFrom().getId());
                if (hasPhoto()) appealTaskArchive.setFile(updateMessagePhoto, FileType.photo);
                if (hasVideo()) appealTaskArchive.setFile(update.getMessage().getVideo().getFileId(), FileType.video);
                if (hasDocument()) appealTaskArchive.setFile(update.getMessage().getDocument().getFileId(), FileType.document);

                List<Director_departments> directors = directorsDepartmentsDao.getAllByAppealTypeId(appealTypeId);

                appealTaskDao.updateStatus(5, taskId);


                if (isButton(54)) {


                    for (Director_departments director: directors) {

                        sendMessageInReceptionType(appealTaskArchive, director.getChatId(), 201);
                    }


                    saveChatId = update.getMessage().getFrom().getId();
                    appealTaskArchiveDao.insertOrUpdate(appealTaskArchive);

                    ReplyKeyboard select = keyboardMarkUpDao.select(102);
//                    uptMess = sendMessage(1059);
                    sendMessageWithKeyboard(messageDao.getMessageText(1059), select);

                    return EXIT;
                }


                appealTaskArchiveDao.insertOrUpdate(appealTaskArchive);
                for (Director_departments director: directors) {

                    sendMessageInReceptionType(appealTaskArchive, director.getChatId(), 201);
                }
                ReplyKeyboard select = keyboardMarkUpDao.select(102);
                uptMess = sendMessageWithKeyboard(messageDao.getMessageText(1059), select);

                return COMEBACK;
        }
        return EXIT;
    }

    private void sendMessageInReceptionType(AppealTaskArchive appealTaskArchive, long chatId, int keyboard) throws TelegramApiException {
        StringBuilder stringBuilder = new StringBuilder();
        statusId = appealTaskDao.getAppealStatusIdByTaskId(appealTaskArchive.getTaskId());
        ReplyKeyboard select = keyboardMarkUpDao.select(keyboard);
//        if (statusId == 5) statusName = getText(1062);
//        if (statusId == 2) statusName = getText(1063);
        user = userDao.getUserById(appealTaskDao.getIdById(taskId).getFullName());
        if (appealTaskArchive.getFile() != null) {
            if (appealTaskArchive.getFileType().name().equals(FileType.photo.name()))
                bot.execute(new SendPhoto().setChatId(chatId).setPhoto(appealTaskArchive.getFile()));
            if (appealTaskArchive.getFileType().name().equals(FileType.video.name()))
                bot.execute(new SendVideo().setChatId(chatId).setVideo(appealTaskArchive.getFile()));
            if (appealTaskArchive.getFileType().name().equals(FileType.document.name()))
                bot.execute(new SendDocument().setChatId(chatId).setDocument(appealTaskArchive.getFile()));
        }
        stringBuilder.append(getText(1049)).append(taskId).append(next);
        stringBuilder.append(getText(1053)).append(appealTaskDao.getIdById(appealTaskArchive.getTaskId()).getText()).append(next);
        stringBuilder.append(getText(1052)).append(user.getFullName()).append(next);
        stringBuilder.append(getText(1068)).append(userDao.getFullNameByChatId(appealEmployeeDao.getChatIdByTegId(appealTaskDao.getAppealTegIdByTaskId(appealTaskArchive.getTaskId())))).append(next);
//        stringBuilder.append(getText(1061)).append(statusName).append(next);
        stringBuilder.append(getText(17)).append(appealTaskArchive.getText()).append(next);
        try {
            uptMess = sendMessageWithKeyboard(stringBuilder.toString(), select, chatId);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private int sendStartDate() throws TelegramApiException {
        return toDeleteKeyboard(sendMessageWithKeyboard(2001, dateKeyboard.getCalendarKeyboard()));
    }

    private void sendMessageToEmployee(String name, Date dateBegin, String text, int taskId, StringBuilder employeeName) {
        StringBuilder messageToEmployee = new StringBuilder();
//        ReplyKeyboard select = keyboardMarkUpDao.select(200);
        String deadlines = dateFormat.format(dateBegin);
        messageToEmployee.append(getText(16)).append(next);
        messageToEmployee.append(getText(1060)).append(updateMessageText).append(next);
        messageToEmployee.append(getText(1049) + taskId).append(next);
        messageToEmployee.append(getText(1052)).append(name).append(next);
        messageToEmployee.append(getText(1053)).append(text).append(next);
        messageToEmployee.append(getText(1054)).append(deadlines).append(next);
        messageToEmployee.append(getText(1050)).append(employeeName);
        for (AppealEmployee employee : allAppealEmployee) {
            long directId = 0;
            if (saveChatId == 0) directId = employee.getChatId();
            if (saveChatId != 0) directId = saveChatId;

            IKeyboardOld kb = new IKeyboardOld();
            kb.next();
            try {
                if (appealTask.getPhoto() != null)
                    bot.execute(new SendPhoto().setChatId(directId).setPhoto(appealTask.getPhoto())).getMessageId();
                if (appealTask.getVideo() != null)
                    bot.execute(new SendVideo().setChatId(directId).setVideo(appealTask.getVideo())).getMessageId();
                SendLocation sendLocation = new SendLocation();
                sendLocation.setLatitude(Float.parseFloat(appealTask.getLocation().split(Const.HESH)[0]));
                sendLocation.setLongitude(Float.parseFloat(appealTask.getLocation().split(Const.HESH)[1]));
                bot.execute(sendLocation.setChatId(directId)).getMessageId();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            try {
//                sendMessageWithKeyboardTest(messageToEmployee.toString(), select, directId);
                sendMessage(messageToEmployee.toString(), directId);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }


    private void sendMessageToAdmins(String name) {



        //  ReplyKeyboard select = keyboardMarkUpDao.select(200);
        for (Admin admin : listOfAdmins) {
            long directId = 0;
            if (saveChatId == 0) directId = admin.getUserId();
            if (saveChatId != 0) directId = saveChatId;

            IKeyboardOld kb = new IKeyboardOld();
            kb.next();
            try {
                sendMessageWithKeyboardTest1(name, directId);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            try {
                sendMessage(String.format(getText(2011), taskId), admin.getUserId());

                if (appealTask.getPhoto() != null)
                    bot.execute(new SendPhoto().setChatId(directId).setPhoto(appealTask.getPhoto())).getMessageId();
                if (appealTask.getVideo() != null)
                    bot.execute(new SendVideo().setChatId(directId).setVideo(appealTask.getVideo())).getMessageId();
                SendLocation sendLocation = new SendLocation();
                sendLocation.setLatitude(Float.parseFloat(appealTask.getLocation().split(Const.HESH)[0]));
                sendLocation.setLongitude(Float.parseFloat(appealTask.getLocation().split(Const.HESH)[1]));
                bot.execute(sendLocation.setChatId(directId)).getMessageId();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }
}
