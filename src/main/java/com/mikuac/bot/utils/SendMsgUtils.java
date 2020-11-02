package com.mikuac.bot.utils;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotContainer;
import net.lz1998.pbbot.utils.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Zero
 * @date 2020/11/2 19:42
 */
@Component
public class SendMsgUtils {

    @Autowired
    private BotContainer botContainer;

    @Value("${yuri.bot.selfId}")
    private Long botId;

    public void sendGroupMsg(Msg msg) {
        Bot bot = botContainer.getBots().get(botId);
        bot.sendGroupMsg(204219849,msg.build(),false);
    }

}
