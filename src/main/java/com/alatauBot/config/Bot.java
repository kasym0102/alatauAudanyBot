package com.alatauBot.config;

import com.alatauBot.dao.DaoFactory;
import com.alatauBot.dao.impl.PropertiesDao;
import com.alatauBot.utils.Const;
import com.alatauBot.utils.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private Map<Long, Conversation> conversations = new HashMap<>();
    private DaoFactory daoFactory    = DaoFactory.getInstance();
    private PropertiesDao propertiesDao = daoFactory.getPropertiesDao();
    private String                  tokenBot;
    private String                  nameBot;

    public  void            onUpdateReceived(Update update) {
        Conversation conversation = getConversation(update);
        try {
            System.out.println(update);
            System.out.println("\n");
            conversation.handleUpdate(update, this);
        } catch (TelegramApiException e) {
            log.error("Error №" + e);
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    private Conversation    getConversation(Update update) {
        Long chatId                 = UpdateUtil.getChatId(update);
        Conversation conversation   = conversations.get(chatId);
        if (conversation == null) {
            log.info("InitNormal new conversation for '{}'", chatId);
            conversation            = new Conversation();
            conversations.put(chatId, conversation);
        }
        return conversation;
    }

    public  String          getBotUsername() {
        if (nameBot == null || nameBot.isEmpty()) nameBot = propertiesDao.getPropertiesValue(Const.BOT_NAME);
        return nameBot;
    }

    public  String          getBotToken() {
        if (tokenBot == null || tokenBot.isEmpty()) tokenBot = propertiesDao.getPropertiesValue(Const.BOT_TOKEN);
        return tokenBot;
    }
}
