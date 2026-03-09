package com.example.Refactoring.config;

/*
 @author kings
 @project Refactoring
 @class TelegramConfig
 @version 1.0.0
 @since 09.03.2026 - 19:33
*/
import com.example.Refactoring.bot.TelegramBot;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;



import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramConfig {

    private final TelegramBot telegramBot;

    public TelegramConfig(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void registerBot() throws Exception {

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(telegramBot);

    }
}