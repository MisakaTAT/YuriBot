package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.config.Global;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 维护模式
 *
 * @author Zero
 * @date 2020/10/23 22:50
 */
@Component
public class Maintenance extends BotPlugin {

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        long userId = event.getUserId();
        if (Global.maintenance_enable) {
            Msg msgBuilder = Msg.builder()
                    .at(userId)
                    .text(Global.maintenance_alertMsg);
            bot.sendGroupMsg(userId, msgBuilder.build(), false);
            return MESSAGE_BLOCK;
        } else {
            return MESSAGE_IGNORE;
        }
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        if (Global.maintenance_enable) {
            Msg msgBuilder = Msg.builder()
                    .at(userId)
                    .text(Global.maintenance_alertMsg);
            bot.sendGroupMsg(groupId, msgBuilder.build(), false);
            return MESSAGE_BLOCK;
        } else {
            return MESSAGE_IGNORE;
        }
    }

}