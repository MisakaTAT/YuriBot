package com.mikuac.bot.plugins;

import com.mikuac.bot.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author Zero
 * @date  2020/10/23 22:50
 */

@Slf4j
@Component
public class Setu extends BotPlugin {

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {


        String msg = event.getRawMessage();

        if(msg.equals("hi")) {
            try {
                String data = HttpClientUtil.httpGetWithJson("https://api.lolicon.app/setu/");
                System.out.println(data);
            } catch (Exception e) {
                //log.info("JSON数据获取异常：{}", e.printStackTrace());
                e.printStackTrace();
            }

        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        return super.onPrivateMessage(bot, event);
    }
}
