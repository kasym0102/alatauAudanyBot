package com.alatauBot.command;

import com.alatauBot.dao.DaoFactory;
import com.alatauBot.dao.impl.*;
import com.alatauBot.entity.custom.AppealTask;
import com.alatauBot.entity.enums.FileType;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.utils.BotUtil;
import com.alatauBot.utils.Const;
import com.alatauBot.utils.SetDeleteMessages;
import com.alatauBot.utils.UpdateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Slf4j
@NoArgsConstructor
public abstract class Command {

    @Getter @Setter
    protected long id;
    @Getter @Setter
    protected long messageId;
//    protected long messageId;

    protected static BotUtil botUtils;
    protected              Update               update;
    protected              DefaultAbsSender     bot;
    protected              Long                 chatId;
    protected              Message              updateMessage;
    protected              String               updateMessageText;
    protected              int                  updateMessageId;
//    protected              int                  upMessId;
    protected              String               editableTextOfMessage;
    protected              String               updateMessagePhoto;
    protected              String               updateMessagePhone;
    protected              String               markChange;
    protected              int                  lastSendMessageID;
    protected final static boolean              EXIT                        = true;
    protected final static boolean              COMEBACK                    = false;
    protected WaitingType waitingType                 = WaitingType.START;
    protected static final String               next                        = "\n";
    protected static final String               space                       = " ";
    protected static final String               hyphen                      = " - ";
    protected              String               nextButton                  = ">>";
    protected              String               prevButton                  = "<<";
    protected              String               plus                        = "❌";

    protected              int                  swap                        = 62;
    protected              int                  snew                        = 63;
    protected              int                  sdell                       = 64;
    protected              int                  sback                       = 65;

    protected static DaoFactory factory                 = DaoFactory.getInstance();
    protected static MessageDao messageDao              = factory.getMessageDao();
    protected static ButtonDao buttonDao               = factory.getButtonDao();
    protected static KeyboardMarkUpDao keyboardMarkUpDao       = factory.getKeyboardMarkUpDao();
    protected static UserDao userDao                 = factory.getUserDao();
    protected static AdminDao adminDao                = factory.getAdminDao();
    protected static    PropertiesDao               propertiesDao           = factory.getPropertiesDao();
    protected static    MapsDao                     mapsDao                 = factory.getMapsDao();
    protected static    ReceptionTypeDao            receptionTypeDao        = factory.getReceptionTypeDao();
    protected static    ReceptionInfoDao            receptionInfoDao        = factory.getReceptionInfoDao();
    protected static    ReceptionEmployeeDao        receptionEmployeeDao    = factory.getReceptionEmployeeDao();
    protected static    CitizensInfoDao             citizensInfoDao         = factory.getCitizensInfoDao();
    protected static    CitizensRegistrationDao      registrationDao        = factory.getCitizensRegistrationDao();
    protected static    AppealTypeDao               appealTypeDao           = factory.getAppealTypeDao();
    protected static    AppealTegDao                appealTegDao            = factory.getAppealTegDao();
    protected static    AppealTaskDao               appealTaskDao           = factory.getAppealTaskDao();
    protected static    DepartmentsTypeDao          departmentTypeDao       = factory.getDepartmentTypeDao();
    protected static    DepartmentsInfoDao          departmentsInfoDao      = factory.getDepartmentsInfoDao();
    protected static    AppealEmployeeDao           appealEmployeeDao       = factory.getAppealEmployeeDao();
    protected static    AppealTaskArchiveDao        appealTaskArchiveDao    = factory.getAppealTaskArchiveDao();
    protected static    AppealTaskRequestToRenewalDao        appealTaskRequestToRenewalDao    = factory.getAppealTaskRequestToRenewalDao();
    protected static    CitizensRegistrationDao     citizensRegistrationDao = factory.getCitizensRegistrationDao();
    protected static    SendMemberDao               sendMemberDao           = factory.getSendMemberDao();
    protected static    AppealTegQuestionAndOptionDao appealTegQuestionAndOptionDao = factory.getAppealTegQuestionAndOptionDao();
    protected static    DirectorsDepartmentsDao directorsDepartmentsDao = factory.getDirectorsDepartmentsDao();

    public abstract boolean execute()                                                           throws TelegramApiException, IOException, SQLException;

    protected int     sendMessageWithKeyboard(int messageId, ReplyKeyboard keyboard)            throws TelegramApiException { return sendMessageWithKeyboard(getText(messageId), keyboard); }

    protected int     sendMessageWithKeyboard(String text, int keyboardId)                      throws TelegramApiException { return sendMessageWithKeyboard(text, keyboardMarkUpDao.select(keyboardId)); }

//    protected int     sendMessageOnlyKeyboard( int keyboardId)                      throws TelegramApiException { return sendMessageOnlyKeyboard(keyboardMarkUpDao.select(keyboardId), chatId); }

    protected int     sendMessageWithKeyboard(String text, ReplyKeyboard keyboard)              throws TelegramApiException {
        lastSendMessageID = sendMessageWithKeyboard(text, keyboard, chatId);
        return lastSendMessageID;
    }

    protected int sendMessGetMessId(int id)      throws TelegramApiException{
        return  bot.execute(new SendMessage().setText(getText(id)).setChatId(chatId).setParseMode(String.valueOf(com.alatauBot.entity.enums.ParseMode.html))).getMessageId();
    }

    protected int sendMessGetMessId(String text)      throws TelegramApiException{
        return  bot.execute(new SendMessage().setText(text).setChatId(chatId).setParseMode(String.valueOf(com.alatauBot.entity.enums.ParseMode.html))).getMessageId();
    }

    protected int     sendMessageWithKeyboard(String text, ReplyKeyboard keyboard, long chatId) throws TelegramApiException { return botUtils.sendMessageWithKeyboard(text, keyboard, chatId); }

//    protected int     sendMessageOnlyKeyboard(ReplyKeyboard keyboard, long chatId) throws TelegramApiException { return botUtils.sendMessageOnlyKeyboard(keyboard, chatId); }

    protected int     sendMessage(String text)                                                  throws TelegramApiException { return sendMessage(text, chatId); }

    protected int     sendMessage(String text, long chatId)                                     throws TelegramApiException { return sendMessage(text, chatId, null); }

    protected int     sendMessage(String text, long chatId, Contact contact)                    throws TelegramApiException {
        lastSendMessageID = botUtils.sendMessage(text, chatId);
        if (contact != null) botUtils.sendContact(chatId, contact);
        return lastSendMessageID;
    }

    protected int     sendMessage(long messageId)                                               throws TelegramApiException { return sendMessage(messageId, chatId); }

    protected int     sendMessage(long messageId, long chatId)                                  throws TelegramApiException { return sendMessage(messageId, chatId, null); }

    protected int     sendMessage(long messageId, long chatId, Contact contact)                 throws TelegramApiException { return sendMessage(messageId, chatId, contact, null); }

    protected int     sendMessage(long messageId, long chatId, Contact contact, String photo)   throws TelegramApiException {
        lastSendMessageID = botUtils.sendMessage(messageId, chatId, contact, photo);
        return lastSendMessageID;
    }

    protected int     toDeleteKeyboard(int messageDeleteId) {
        SetDeleteMessages.addKeyboard(chatId, messageDeleteId);
        return messageDeleteId;
    }

    protected int     sendMessageWithKeyboardTest(String text, ReplyKeyboard keyboard, long chatID) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage().setParseMode(ParseMode.HTML).setChatId(chatID).setText(text).setReplyMarkup(keyboard);
        sendMessageTest(text, sendMessage);
        return lastSendMessageID;
    }

    protected int     sendMessageWithKeyboardTest1(String text,  long chatID) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage().setParseMode(ParseMode.HTML).setChatId(chatID).setText(text);
        sendMessageTest(text, sendMessage);
        return lastSendMessageID;
    }

    protected int     toDeleteMessage(int messageDeleteId) {
        SetDeleteMessages.addKeyboard(chatId, messageDeleteId);
        return messageDeleteId;
    }

    public    void    clear() {
        update  = null;
        bot     = null;
    }

    protected void    deleteMessage(int messageId) { deleteMessage(chatId, messageId); }

    protected void    deleteMessage(long chatId, int messageId) { botUtils.deleteMessage(chatId, messageId); }

    private   void    sendMessageTest(String text, SendMessage sendMessage)                     throws TelegramApiException {
        try {
            lastSendMessageID = bot.execute(sendMessage).getMessageId();
        } catch (TelegramApiRequestException e) {
            if (e.getApiResponse().contains("Bad Request: can't parse entities")) {
                sendMessage.setParseMode(null);
                sendMessage.setText(text + next + "Wrong number");
                lastSendMessageID = bot.execute(sendMessage).getMessageId();
            } else throw e;
        }
    }

    protected void    sendMessageWithAddition()                                                 throws TelegramApiException {
        deleteMessage(updateMessageId);
        com.alatauBot.entity.standart.Message message = messageDao.getMessage(messageId);
        sendMessage(messageId, chatId, null, message.getPhoto());
        try {
            if (message.getFile() != null) {
                switch (message.getFileType()) {
                    case audio:
                        bot.execute(new SendAudio().setAudio(message.getFile()).setChatId(chatId));
                    case video:
                        bot.execute(new SendVideo().setVideo(message.getFile()).setChatId(chatId));
                    case document:
                        bot.execute(new SendDocument().setChatId(chatId).setDocument(message.getFile()));
                }
            }
        } catch (TelegramApiException e) {
            log.error("Exception by send file for message " + messageId, e);
        }
    }

    protected String  getLinkForUser(long chatId, String userName) { return String.format("<a href = \"tg://user?id=%s\">%s</a>", chatId, userName); }

    protected String  getText(int messageIdFromDb) { return messageDao.getMessageText(messageIdFromDb); }

    public    boolean isInitNormal(Update update, DefaultAbsSender bot) {
        if (botUtils == null) botUtils = new BotUtil(bot);
        this.update = update;
        this.bot    = bot;
        chatId      = UpdateUtil.getChatId(update);
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            updateMessage               = callbackQuery.getMessage();
            updateMessageText           = callbackQuery.getData();
            updateMessageId             = updateMessage.getMessageId();
            editableTextOfMessage       = callbackQuery.getMessage().getText();
        } else if (update.hasMessage()) {
            updateMessage               = update.getMessage();
            updateMessageId             = updateMessage.getMessageId();
            if (updateMessage.hasText()) updateMessageText = updateMessage.getText();
            if (updateMessage.hasPhoto()) {
                int size                = update.getMessage().getPhoto().size();
                updateMessagePhoto      = update.getMessage().getPhoto().get(size - 1).getFileId();
            } else {
                updateMessagePhoto      = null;
            }
        }
        if (hasContact()) updateMessagePhone = update.getMessage().getContact().getPhoneNumber();
        if (markChange == null) markChange = getText(Const.  EDIT_BUTTON_ICON);
        return false;
    }

    protected boolean isRegistered() { return userDao.isRegistered(chatId); }

    protected boolean isAdmin() { return adminDao.isAdmin(chatId); }

    protected boolean hasContact() { return update.hasMessage() && update.getMessage().getContact() != null; }

    protected boolean isButton(int buttonId) { return updateMessageText.equals(buttonDao.getButtonText(buttonId)); }

    protected boolean hasCallbackQuery() { return update.hasCallbackQuery();}

    protected boolean hasMessageText() { return update.hasMessage() && update.getMessage().hasText(); }

    protected boolean hasPhoto() { return update.hasMessage() && update.getMessage().hasPhoto(); }

    protected boolean hasDocument() { return update.hasMessage() && update.getMessage().hasDocument(); }

    protected boolean hasAudio() { return update.hasMessage() && update.getMessage().getAudio() != null; }

    protected boolean hasVideo() { return update.hasMessage() && update.getMessage().getVideo() != null; }

    protected String  getBolt(String s) { return "<b>" + s + "</b>"; }

    protected String getButtonText(int id){ return buttonDao.getButtonText(id); }

    protected int onlyNumbers(String strNum){ return Integer.parseInt(strNum.replaceAll("[^0-9]", "")); }

    protected boolean isReceptionEmployee() { return factory.getReceptionEmployeeDao().isReceptionEmployee(chatId); }

    protected boolean isEmployeeCategory() { return factory.getAppealEmployeeDao().isEmployeeCategory(chatId); }

    protected void deleteAll(int updMes) { deleteMessage(updateMessageId); deleteMessage(updMes); }

    protected static void sort(List<AppealTask> list) {
        sort(list, 0, list.size() - 1);
    }

    protected static void sort(List<AppealTask> list, int from, int to) {
        if (from < to) {
            int pivot = from;
            int left = from + 1;
            int right = to;
            int pivotValue = list.get(pivot).getId();
            while (left <= right) {
                // left <= to -> limit protection
                while (left <= to && pivotValue >= list.get(left).getId()) {
                    left++;
                }
                // right > from -> limit protection
                while (right > from && pivotValue < list.get(right).getId()) {
                    right--;
                }
                if (left < right) {
                    Collections.swap(list, left, right);
                }
            }
            Collections.swap(list, pivot, left - 1);
            sort(list, from, right - 1); // <-- pivot was wrong!
            sort(list, right + 1, to);   // <-- pivot was wrong!
        }
    }
}
