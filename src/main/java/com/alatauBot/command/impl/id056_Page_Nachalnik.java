package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.custom.*;
import com.alatauBot.entity.enums.FileType;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.entity.standart.User;
import com.alatauBot.utils.ButtonsLeaf;
import com.alatauBot.utils.Const;
import com.alatauBot.utils.DateKeyboard;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class id056_Page_Nachalnik extends Command {

    private int uptMes;
    private ButtonsLeaf buttonsLeaf;
    private List<String> list = new ArrayList<>();
    private List<AppealTaskArchive > taskArchives = new ArrayList<>();
    private List<AppealTask> tasks = new ArrayList<>();
    private User user = new User();
    private AppealTask appealTask;
    private String text, statusName;
    private int statusId;
    private int uptMess;
    private long saveChatId;
    private List<AppealEmployee> allAppealEmployee;
    private int taskId;
    private AppealTaskArchive appealTaskArchive;
    private AppealTaskRequestToRenewal appealTaskRequestToRenewal = new AppealTaskRequestToRenewal();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        switch (waitingType) {
            case START:
                deleteAll(uptMes);
                if (isDirector(chatId.intValue())) {

                    if (isButton(Const.APPROVED)) { // TODO: PRESS TO BUTTON APPROVED // кнопка одобрено
                        text = update.getCallbackQuery().getMessage().getText();
                        taskId = onlyNumbers(text.split(next)[0]);
                        appealTask = appealTaskDao.getIdById(taskId);

                        user = userDao.getUserById(appealTask.getFullName());
                        appealTaskArchive = appealTaskArchiveDao.getOneByTaskId(taskId);
                        appealTaskDao.updateStatus(1, taskId);
                        sendMessageInReceptionType(appealTaskArchive, user.getChatId(), 202);



                    }
                    if (isButton(Const.NOT_APPROVED)) { // TODO: PRESS TO BUTTON NOT APPROVED // кнопка не одобрено

                        text = update.getCallbackQuery().getMessage().getText();
                        taskId = onlyNumbers(text.split(next)[0]);
                        appealTask = appealTaskDao.getIdById(taskId);

                        deleteAll(uptMess);
                        uptMess = sendMessage(1056);
                        waitingType = WaitingType.SET_TEXT_DESC;
                        return COMEBACK;
                    }

                    if (isButton(73)){
                        deleteAll(uptMes);
                        //todo список запросов на одобрение
                        requests_responsible();
                        waitingType = WaitingType.CHOOSE_REQUEST_RESPONSIBLE_NACH;
                        return COMEBACK;
                    }

                    uptMes = sendMessageWithKeyboard(getText(52), 18);
                    waitingType = WaitingType.REQUESTS_FOR_APPROVAL;
                    return COMEBACK;

                }
                else {
                    sendMessage(14);
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
            case CHOOSE_REQUEST_RESPONSIBLE_NACH:
                choose_request();
                return COMEBACK;

            default: return COMEBACK;
        }
    }


    private boolean isDirector(int chatId){
        return directorsDepartmentsDao.isDirector(chatId);
    }

    private void requests_responsible() throws TelegramApiException {
        list.clear();
        List<Director_departments> directorOfDepartments =  directorsDepartmentsDao.getAllByChatId(chatId);

        for (Director_departments director: directorOfDepartments) {
            tasks.addAll(appealTaskDao.getAllByAppealTypeIdAndStatus(director.getAppealType_id(), 5));  // status 5 = на рассмотрений у начальника
        }
        for (AppealTask task: tasks){
            list.add("№ " + task.getId());
        }
        buttonsLeaf = new ButtonsLeaf(list);

        if (list.size() == 0){
            uptMes = toDeleteKeyboard(sendMessageWithKeyboard(getText(54), 18));

        }
        else uptMes = toDeleteKeyboard(sendMessageWithKeyboard(53, buttonsLeaf.getListButtonWhereDataIsName()));
    }

    private void choose_request() throws TelegramApiException {
        deleteAll(uptMes);
        int appealTaskId = Integer.parseInt(updateMessageText.split(" ")[1]);

        StringBuilder stringBuilder = new StringBuilder();
        User user =  userDao.getUserById(appealTaskDao.getIdById(appealTaskId).getFullName());


        AppealTask appealTask = appealTaskDao.getIdById(appealTaskId); // не обязательно

        AppealTaskArchive taskArchive = appealTaskArchiveDao.getOneByTaskId(appealTaskId);

        if (taskArchive.getFile() != null) {
            if (taskArchive.getFileType().name().equals(FileType.photo.name()))
                bot.execute(new SendPhoto().setChatId(chatId).setPhoto(taskArchive.getFile()));
            if (taskArchive.getFileType().name().equals(FileType.video.name()))
                bot.execute(new SendVideo().setChatId(chatId).setVideo(taskArchive.getFile()));
            if (taskArchive.getFileType().name().equals(FileType.document.name()))
                bot.execute(new SendDocument().setChatId(chatId).setDocument(taskArchive.getFile()));
        }

        stringBuilder.append(getText(1049)).append(appealTaskId).append(next);
        stringBuilder.append(getText(1053)).append(appealTaskDao.getIdById(taskArchive.getTaskId()).getText()).append(next);
        stringBuilder.append(getText(1052)).append(user.getFullName()).append(next);
        stringBuilder.append(getText(1068)).append(userDao.getFullNameByChatId(appealEmployeeDao.getChatIdByTegId(appealTask.getAppealTegId()))).append(next);
        stringBuilder.append(getText(1061)).append(getText(1062)).append(next);
        stringBuilder.append(getText(17)).append(taskArchive.getText()).append(next);
        try {
            uptMes = sendMessageWithKeyboard(stringBuilder.toString(), 201);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }



//        uptMess = toDeleteKeyboard(sendMessageWithKeyboard(stringBuilder.toString(), 1008));
    }

    private void sendMessageInReceptionType(AppealTaskArchive appealTaskArchive, long chatId, int keyboard) throws TelegramApiException {
        StringBuilder stringBuilder = new StringBuilder();
//        statusId = appealTaskDao.getAppealStatusIdByTaskId(appealTaskArchive.getTaskId());
        ReplyKeyboard select = keyboardMarkUpDao.select(keyboard);
//        if (statusId == 5)
//        if (statusId == 2) statusName = getText(1063);

        statusName = getText(1062);

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
        stringBuilder.append(getText(1061)).append(statusName).append(next);
        stringBuilder.append(getText(17)).append(appealTaskArchive.getText()).append(next);
        try {
            uptMess = sendMessageWithKeyboard(stringBuilder.toString(), select, chatId);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToEmployee(String name, Date dateBegin, String text, int taskId, StringBuilder employeeName) {
        StringBuilder messageToEmployee = new StringBuilder();

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
                sendMessage(messageToEmployee.toString(), directId);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

}
