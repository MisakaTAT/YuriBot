package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.config.Global;
import com.mikuac.shiro.bot.Bot;
import com.mikuac.shiro.bot.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.utils.Msg;
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
    public int onPrivateMessage(@NotNull Bot bot, PrivateMessageEvent event) {
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
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
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