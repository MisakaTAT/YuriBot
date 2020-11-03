package com.mikuac.bot.plugins;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 维护模式
 * @author Zero
 * @date  2020/10/23 22:50
 */
@Component
public class Maintenance extends BotPlugin {

    @Value("${yuri.plugins.maintenance-config.isMaintenance}")
    private Boolean isMaintenance;
    @Value("${yuri.plugins.maintenance-config.maintenanceMsg}")
    private String maintenanceMsg;

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        long userId = event.getUserId();
        if (isMaintenance){
            Msg msgBuilder = Msg.builder()
                    .at(userId)
                    .text(maintenanceMsg);
            bot.sendGroupMsg(userId,msgBuilder.build(),false);
            return MESSAGE_BLOCK;
        }else {
            return MESSAGE_IGNORE;
        }
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        if (isMaintenance){
            Msg msgBuilder = Msg.builder()
                    .at(userId)
                    .text(maintenanceMsg);
            bot.sendGroupMsg(groupId,msgBuilder.build(),false);
            return MESSAGE_BLOCK;
        }else {
            return MESSAGE_IGNORE;
        }
    }

}