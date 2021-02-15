package com.alatauBot;

import com.alatauBot.config.Bot;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@Slf4j
public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        log.info("ApiContextInitializer.InitNormal()");
        System.out.println("ApiContextInitializer.InitNormal()");
        Bot bot = new Bot();
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            telegramBotsApi.registerBot(bot);
            System.out.println("Bot was registered: " + bot.getBotUsername());
            log.info("Bot was registered: " + bot.getBotUsername());
        }catch (TelegramApiRequestException e){
            System.out.println("TelegramApiRequestException " + e);
            log.info("TelegramApiRequestException " + e);
        }

    }
}
