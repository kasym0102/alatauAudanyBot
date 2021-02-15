package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.enums.WaitingType;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;

public class id040_Photo extends Command {
    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                sendMessage("Отправь фото/файл/аудио/видео");
                waitingType = WaitingType.SET_PHOTO;
                return COMEBACK;
            case SET_PHOTO:
                deleteMessage(updateMessageId);
                if (hasPhoto()) {
                    sendMessage(updateMessagePhoto);
                }else if (hasDocument()){
                    sendMessage(update.getMessage().getDocument().getFileId());
                }else if (hasAudio()){
                    sendMessage(update.getMessage().getAudio().getFileId());
                }else if (hasVideo()){
                    sendMessage(update.getMessage().getVideo().getFileId());
                }else if (update.getMessage().hasSticker()){
                    sendMessage(update.getMessage().getSticker().getFileId());
                }
                return EXIT;
        }
        return EXIT;
    }
}
