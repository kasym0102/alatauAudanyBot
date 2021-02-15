package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.entity.standart.Admin;
import com.alatauBot.entity.standart.User;
import com.alatauBot.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class id009_EditOrAddAdmin extends Command {
//    private long admin;
    private User user;
    private List<String> list;
    private List<User> users;
    private List<Admin> admins;
    private Admin admin;
    private String adminText;
    private int curId, i, uptMess;
    private StringBuilder stringBuilder;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        if (!isAdmin()){
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType){
            case START:
                deleteAll(uptMess);
                list = new ArrayList<>();
                menuAdminInfo();
                waitingType = WaitingType.DELETE_ADMIN;
                return COMEBACK;
            case DELETE_ADMIN:
                deleteAll(uptMess);
                if (updateMessageText.equals("/addAdmin")){
                    uptMess = toDeleteMessage(sendMessage(1011));
                    waitingType = WaitingType.ADD_ADMIN;
                    return COMEBACK;
                }
                curId = Integer.parseInt(updateMessageText.replaceAll("[^0-9]","")) - 1;
                adminDao.delete(admins.get(curId).getUserId());
                menuAdminInfo();
                waitingType = WaitingType.DELETE_ADMIN;
                return COMEBACK;
            case ADD_ADMIN:
                deleteAll(uptMess);
                if (update.getMessage().hasContact()){
                    admin = new Admin();
                    admin.setUserId(update.getMessage().getContact().getUserID().longValue());
                    admin.setComment(update.getMessage().getContact().getFirstName() + " " + update.getMessage().getContact().getLastName());
                    adminDao.insertAdmin(admin);
                    menuAdminInfo();
                    waitingType = WaitingType.DELETE_ADMIN;
                }
                return COMEBACK;
        }
        return EXIT;
    }
    
    public void menuAdminInfo() throws TelegramApiException {
        list.clear();
        i = 0;
        stringBuilder = new StringBuilder();
        admins = Optional.ofNullable(adminDao.getAll()).map(admins -> { admins.forEach(admin -> list.add(admin.getComment())); return admins; }).orElse(new ArrayList<>());
        stringBuilder.append("Список администраторов:").append(next);
        list.forEach(l -> stringBuilder.append("/dell").append(++i).append(" - ").append(l).append(next));
        stringBuilder.append("/addAdmin");
        uptMess = toDeleteMessage(sendMessage(stringBuilder.toString()));
    }
}
