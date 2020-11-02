package com.mikuac.bot.bean;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Zero
 * @date 2020/11/2 16:38
 */
@Data
@Component
public class ConfigGet {

    @Value("${yuri.telegram.botName}")
    private String botName;

    @Value("${yuri.telegram.botToken}")
    private String botToken;

}
