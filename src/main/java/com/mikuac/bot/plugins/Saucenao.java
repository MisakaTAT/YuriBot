package com.mikuac.bot.plugins;

import net.lz1998.pbbot.bot.BotPlugin;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zero
 * @date 2020/12/3 11:45
 */
@Component
public class Saucenao extends BotPlugin {

    /**
     * 初始化dbMap
     */
    Map<String, String> dbMap = new ConcurrentHashMap<>() {
        {
            put("all", "999");
            put("pixiv", "5");
            put("danbooru", "9");
            put("doujin", "18");
            put("anime", "21");
        }
    };

}
