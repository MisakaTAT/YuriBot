package com.mikuac.bot.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author Zero
 * @date 2020/10/31 17:08
 */
@Component
public class ForwardMessage extends TelegramLongPollingBot {

    @Value("${yuri.telegram.botName}")
    private String botName;

    @Value("${yuri.telegram.botToken}")
    private String botToken;

    static {
        // 初始化Api上下文
        ApiContextInitializer.init();
        // 实例化Telegram Bots API
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            // 注册Bot
            telegramBotsApi.registerBot(new ForwardMessage());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        String msg = update.getMessage().getText();
        System.out.println(msg);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

}
