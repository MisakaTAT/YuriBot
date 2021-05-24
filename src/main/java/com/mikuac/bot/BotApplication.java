package com.mikuac.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.telegram.telegrambots.ApiContextInitializer;

/**
 * @author Zero
 * @create 2020/10/23 22:50
 */
@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class BotApplication {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.initialize();
        return taskScheduler;
    }

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
        // 初始化Telegram框架
        ApiContextInitializer.init();
        log.info("你好~ 我是悠里⁄(⁄ ⁄•⁄ω⁄•⁄ ⁄)⁄");
    }

}
