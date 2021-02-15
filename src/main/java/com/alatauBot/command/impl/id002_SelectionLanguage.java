package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.enums.Language;
import com.alatauBot.services.LanguageService;
import com.alatauBot.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class id002_SelectionLanguage extends Command {

    @Override
    public boolean  execute() throws TelegramApiException {
        deleteMessage(updateMessageId);
        chosenLanguage();
        toDeleteMessage(sendMessage(Const.WELCOME_TEXT_WHEN_START));
        return EXIT;
    }

    private void    chosenLanguage() {
        if (isButton(Const.RU_LANGUAGE)) LanguageService.setLanguage(chatId, Language.ru);
        if (isButton(Const.KZ_LANGUAGE)) LanguageService.setLanguage(chatId, Language.kz);
    }
}
