package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.config.RegexConst;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 发送帮助链接
 * @author Zero
 * @date 2020/12/2 11:09
 */
@Component
public class SendHelp extends BotPlugin {

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        long userId = event.getUserId();
        String msg = event.getRawMessage();
        if (msg.matches(RegexConst.SEND_HELP)) {
            bot.sendPrivateMsg(userId,"https://mikuac.com/archives/675",false);
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        if (msg.matches(RegexConst.SEND_HELP)) {
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("https://mikuac.com/archives/675/").build(),false);
        }
        return MESSAGE_IGNORE;
    }

}