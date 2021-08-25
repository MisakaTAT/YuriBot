package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.BotApplication;
import com.mikuac.bot.config.Global;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Created on 2021/7/19.
 *
 * @author Zero
 */
@Component
public class ShutdownBot extends BotPlugin {

    @SneakyThrows
    @Override
    public int onPrivateMessage(@NotNull Bot bot, PrivateMessageEvent event) {
        String msg = event.getMessage();
        long userId = event.getUserId();
        if (msg.matches(RegexConst.SHUTDOWN_BOT)) {
            if (Global.botAdminId != userId) {
                bot.sendPrivateMsg(userId, "此操作仅管理员可执行", false);
                return MESSAGE_IGNORE;
            }
            bot.sendPrivateMsg(userId, Global.botBotName + "服务停止中，正在释放资源，请耐心等待～", false);
            BotApplication.shutdown();
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String msg = event.getMessage();
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        if (msg.matches(RegexConst.SHUTDOWN_BOT)) {
            if (Global.botAdminId != userId) {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("此操作仅管理员可执行").build(), false);
                return MESSAGE_IGNORE;
            }
            bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text(Global.botBotName + "服务停止中，正在释放资源，请耐心等待～").build(), false);
            BotApplication.shutdown();
        }
        return MESSAGE_IGNORE;
    }

}
