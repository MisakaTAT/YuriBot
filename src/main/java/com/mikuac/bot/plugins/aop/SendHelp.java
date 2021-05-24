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
 *
 * @author Zero
 * @date 2020/12/2 11:09
 */
@Component
public class SendHelp extends BotPlugin {


    public Msg buildMsg(Boolean isGroupMsg, long userId) {
        Msg sendMsg = Msg.builder();
        if (isGroupMsg) {
            sendMsg.at(userId).text("\n");
        }
        sendMsg.text("操作文档: https://mikuac.com/archives/675");
        sendMsg.text("\n项目地址: https://github.com/MisakaTAT/YuriBot");
        return sendMsg;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        long userId = event.getUserId();
        String msg = event.getRawMessage();
        if (msg.matches(RegexConst.SEND_HELP)) {
            bot.sendPrivateMsg(userId, buildMsg(false, userId).build(), false);
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        if (msg.matches(RegexConst.SEND_HELP)) {
            bot.sendGroupMsg(groupId, buildMsg(true, userId).build(), false);
        }
        return MESSAGE_IGNORE;
    }

}
