package com.alatauBot.command.impl;

import com.alatauBot.services.LanguageService;
import com.alatauBot.command.Command;
import com.alatauBot.entity.enums.FileType;
import com.alatauBot.entity.enums.Language;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.entity.standart.Button;
import com.alatauBot.entity.standart.Message;
import com.alatauBot.utils.ButtonUtil;
import com.alatauBot.utils.Const;
import com.alatauBot.utils.ParserMessageEntity;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class id012_EditMenu extends Command {
    private Language currentLanguage;
    private              int      buttonId;
    private              long     keyboardMarkUpId;
    private Button currentButton;
    private              int      textId;
    private              int      photoId;
    private Message message;
    private              int      keyId;
    private static final String   linkEdit = "/linkId";
    private              boolean  isUrl    = false;
    private              int      buttonLinkId;
    private final static String   NAME     = messageDao.getMessageText(1043);
    private final static String   LINK     = messageDao.getMessageText(1044);
    private              int      delMesId;
    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                currentLanguage = LanguageService.getLanguage(chatId);
                sendListMenu();
                return COMEBACK;
            case CHOICE_OPTION:
                deleteMessage(updateMessageId);
                if (hasCallbackQuery()) {
                    buttonId                = Integer.parseInt(updateMessageText);
                    if (buttonDao.getButton(buttonId, currentLanguage).getMessageId() != 0) keyboardMarkUpId = messageDao.getMessage(buttonDao.getButton(buttonId, currentLanguage).getMessageId()).getKeyboardMarkUpId();
                    currentButton           = buttonDao.getButton(buttonId, currentLanguage);
                    sendEditor();
                    loadElements();
                } else {
                    sendListMenu();
                }
                return COMEBACK;
            case NEXT_KEYBOARD:
                if (hasCallbackQuery()) {
                    buttonId        = Integer.parseInt(updateMessageText);
                    currentButton   = buttonDao.getButton(buttonId, currentLanguage);
                    waitingType     = WaitingType.CHOICE_OPTION;
                    return COMEBACK;
                } else {
                    sendListMenu();
                }
                return COMEBACK;
            case COMMAND_EDITOR:
                isCommand();
                return COMEBACK;
            case UPDATE_BUTTON:
                if (isCommand()) return COMEBACK;
                if (hasMessageText()) {
                    String buttonName = (ButtonUtil.getButtonName(updateMessageText,100));
                    if (buttonName.replaceAll("[0-9]", "").isEmpty()) {
                        sendMessage(1037);
                        return COMEBACK;
                    }
                    if (buttonDao.isExist(buttonName, currentLanguage)) {
                        sendMessage(1038);
                        return COMEBACK;
                    }
                    currentButton.setName(buttonName);
                    buttonDao.update(currentButton);
                    sendEditor();
                    return COMEBACK;
                }
                return COMEBACK;
            case UPDATE_TEXT:
                if (isCommand()) return COMEBACK;
                if (hasMessageText()) {
                    message.setName(new ParserMessageEntity().getTextWithEntity(update.getMessage()));
                    messageDao.update(message);
                    sendEditor();
                    return COMEBACK;
                }
                return COMEBACK;
            case UPDATE_BUTTON_LINK:
                if (isCommand()) return COMEBACK;
                if (hasMessageText()) {
                    if (updateMessageText.startsWith(NAME)) {
                        String buttonName   = ButtonUtil.getButtonName(updateMessageText.replace(NAME,""));
                        if (buttonDao.isExist(buttonName, currentLanguage)) {
                            sendMessage(1038);
                            return COMEBACK;
                        }
                        Button button       = buttonDao.getButton(buttonLinkId, currentLanguage);
                        button.setName(buttonName);
                        buttonDao.update(button);
                        sendEditor();
                        return COMEBACK;
                    } else if (updateMessageText.startsWith(LINK)) {
                        Button button = buttonDao.getButton(buttonLinkId, currentLanguage);
                        button.setUrl(updateMessageText.replace(LINK,""));
                        buttonDao.update(button);
                        sendEditor();
                        return COMEBACK;
                    } else {
                        sendMessage(1042);
                    }
                }
                sendMessage(1042);
                return COMEBACK;
            case UPDATE_FILE:
                if (hasDocument() || hasAudio() || hasVideo() || hasPhoto()) {
//                    if (!isHasMessageForEdit()) return COMEBACK;
                    updateFile();
                    sendMessage(1045);
                    sendEditor();
                    return COMEBACK;
                }
        }
        return EXIT;
    }
    private void        sendListMenu()          throws TelegramApiException {
        toDeleteKeyboard(sendMessageWithKeyboard(1031, keyboardMarkUpDao.selectForEdition(3, currentLanguage)));
        waitingType = WaitingType.CHOICE_OPTION;
    }

    private void        sendEditor()            throws TelegramApiException {
        clearOld();
        loadElements();
        String desc;
        if (message != null) {
            keyId = (int) message.getKeyboardMarkUpId();
            if (message.getPhoto() != null) photoId = bot.execute(new SendPhoto().setPhoto(message.getPhoto()).setChatId(chatId)).getMessageId();
            StringBuilder urlList   = new StringBuilder();
            if (keyId != 0 && keyboardMarkUpDao.isInline(keyId)) {
                urlList.append(getText(1032)).append(next);
                List<Button> list   = keyboardMarkUpDao.getListForEdit(keyId);
                for (Button button : list) {
                    if (button.getUrl() != null) urlList.append(linkEdit).append(button.getId()).append(" ").append(button.getName()).append(" - ").append(button.getUrl()).append(next);
                }
            }
            desc = String.format(getText(1033), currentButton.getName(), message.getName(), urlList, currentLanguage.name()); //
            if (desc.length() > getMaxSizeMessage()) {
                String substring = message.getName().substring(0, desc.length() - getMaxSizeMessage() - 3) + "...";
                desc = String.format(getText(1033), currentButton.getName(), substring, currentLanguage.name());
            }
        } else {
            desc = String.format(getText(1033), currentButton.getName(), getText(1034), currentLanguage.name());
        }
        textId      = sendMessageWithKeyboard(desc, 12);
        toDeleteKeyboard(textId);
        waitingType = WaitingType.COMMAND_EDITOR;
    }

    private void        clearOld() {
        deleteMessage(textId);
        deleteMessage(photoId);
    }

    private void        loadElements() {
        if (currentButton.getMessageId() == 0) {
            message = null;
        } else {
            message = messageDao.getMessage(currentButton.getMessageId(), currentLanguage);
        }
    }

    private static int  getMaxSizeMessage() { return 4096; }

    private boolean     isCommand()             throws TelegramApiException {
        deleteMessage(updateMessageId);
        if (hasPhoto()) {
            if (!isHasMessageForEdit()) return COMEBACK;
            updatePhoto();
        } else if (hasDocument() || hasAudio() || hasVideo()) {
            if (!isHasMessageForEdit()) return COMEBACK;
            updateFile();
        } else if (isButton(40)) {
            sendMessage(1036);
            waitingType = WaitingType.UPDATE_BUTTON;
            return EXIT;
        } else if (isButton(41)) {
            if (!isHasMessageForEdit()) return COMEBACK;
            sendMessage(1039);
            waitingType = WaitingType.UPDATE_TEXT;
            return EXIT;
        } else if (isButton(44)) { // TODO: 11.02.2020 создать константы для дб
            sendMessage(1040);
            waitingType = WaitingType.UPDATE_FILE;
            return EXIT;
        } else if (isButton(45)) {
            if (!isHasMessageForEdit()) return COMEBACK;
            deleteFile();
        } else if (isButton(46)) {
            if (currentLanguage == Language.ru) {
                currentLanguage = Language.kz;
            } else {
                currentLanguage = Language.ru;
            }
            currentButton = buttonDao.getButton(buttonId, currentLanguage);
            sendEditor();
            return EXIT;
        } else if (isButton(47)) {
            deleteMessage(updateMessageId);
            deleteMessage(textId);
            if (keyboardMarkUpId != 0) isUrl = getButtonIds((int) keyboardMarkUpId);
            if (keyboardMarkUpId == 2) {
                currentButton = buttonDao.getButton(buttonId, currentLanguage);
                sendEditor();
                return COMEBACK;
            } else if (keyboardMarkUpId > 0) {
                if (!isUrl) {
                    toDeleteKeyboard(sendMessageWithKeyboard(1041, keyboardMarkUpDao.selectForEdition(keyboardMarkUpId, currentLanguage)));
                    waitingType = WaitingType.NEXT_KEYBOARD;
                } else {
                    currentButton = buttonDao.getButton(buttonId, currentLanguage);
                    sendEditor();
                    return COMEBACK;
                }
            }
        } else if (updateMessageText.startsWith(linkEdit)) {
            String buttId       = updateMessageText.replace(linkEdit, "");
            if (keyboardMarkUpDao.getButtonString(keyId).contains(buttId)) {
                sendMessage(1042);
                buttonLinkId    = Integer.parseInt(buttId);
                waitingType     = WaitingType.UPDATE_BUTTON_LINK;
                return EXIT;
            } else {
                return COMEBACK;
            }
        } else {
            return COMEBACK;
        }
        return EXIT;
    }

    private boolean     isHasMessageForEdit()   throws TelegramApiException {
        if (message == null) {
            sendMessage(1035);
            return COMEBACK;
        }
        return EXIT;
    }

    private void        updatePhoto() {
        message.setPhoto(updateMessagePhoto);
        update();
    }

    private void        update() {
        messageDao.update(message);
        log.info("Update message {} for lang {} - chatId = ", message.getId(), currentLanguage.name(), chatId);
    }

    private void        updateFile() {
        if (hasDocument()) {
            message.setFile(update.getMessage().getDocument().getFileId(), FileType.document);
        } else if (hasAudio()) {
            message.setFile(update.getMessage().getAudio().getFileId(), FileType.audio);
        } else if (hasVideo()) {
            message.setFile(update.getMessage().getVideo().getFileId(), FileType.video);
        } else if (hasPhoto()) {
            message.setFile(updateMessagePhoto, FileType.photo);
        }
        update();
    }

    private void        deleteFile() {
        message.setFileType(null);
        message.setFile(null);
        update();
    }

    private boolean     getButtonIds(int keyboardMarkUpId) {
        String buttonsString        = keyboardMarkUpDao.getButtonString(keyboardMarkUpId);
        if (buttonsString == null) return COMEBACK;
        String rows[]               = buttonsString.split(";");
        for (String buttonIdString : rows) {
            String[] buttonIds      = buttonIdString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = buttonDao.getButton(Integer.parseInt(buttonId), currentLanguage);
                String url          = buttonFromDb.getUrl();
                if (url != null) {
                    return EXIT;
                } else {
                    return COMEBACK;
                }
            }
        }
        return COMEBACK;
    }
}
