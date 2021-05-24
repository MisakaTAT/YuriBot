package com.mikuac.bot;

import com.mikuac.bot.config.Global;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.telegram.telegrambots.ApiContextInitializer;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

    @Bean
    @SuppressWarnings("rawtypes")
    public WebServerFactoryCustomizer webServerFactoryCustomizer() {
        return (WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>) factory -> {
            try {
                factory.setAddress(InetAddress.getByName(Global.config.getServer().getAddress()));
            } catch (UnknownHostException e) {
                log.error("启动地址设置失败: {}", e.getMessage());
            }
            factory.setPort(Global.config.getServer().getPort());
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
        // 初始化Telegram框架
        ApiContextInitializer.init();
        log.info("你好~ 我是悠里⁄(⁄ ⁄•⁄ω⁄•⁄ ⁄)⁄");
    }

}
