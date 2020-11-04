package com.mikuac.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

/**
 * @author Zero
 * @create 2020/10/23 22:50
 */
@SpringBootApplication
public class BotApplication {
    public static void main(String[] args) {
        // Telegram框架初始化
        ApiContextInitializer.init();
        SpringApplication.run(BotApplication.class, args);
    }
}