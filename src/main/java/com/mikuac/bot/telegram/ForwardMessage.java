package com.mikuac.bot.telegram;

import com.mikuac.bot.bean.ConfigGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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
public class ForwardMessage extends TelegramLongPollingBot implements ApplicationRunner {

    private ConfigGet configGet;

    @Autowired
    public void setConfigGet(ConfigGet configGet) {
        this.configGet = configGet;
    }

    @Override
    public void run(ApplicationArguments args) {
        // 初始化Api上下文
        ApiContextInitializer.init();
        // 实例化Telegram Bots API
        TelegramBotsApi bot = new TelegramBotsApi();
        try {
            // 注册Bot
            bot.registerBot(new ForwardMessage());
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
        return configGet.getBotToken();
    }

    @Override
    public String getBotToken() {
        return configGet.getBotToken();
    }

}