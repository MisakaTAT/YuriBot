package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.BotApplication;
import com.mikuac.bot.config.Global;
import com.mikuac.bot.config.RegexConst;
import lombok.SneakyThrows;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
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
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        if (msg.matches(RegexConst.SHUTDOWN_BOT)) {
            if (Global.bot_adminId != userId) {
                bot.sendPrivateMsg(userId, "此操作仅管理员可执行", false);
                return MESSAGE_IGNORE;
            }
            bot.sendPrivateMsg(userId, Global.bot_botName + "服务停止中，正在释放资源，请耐心等待～", false);
            BotApplication.shutdown();
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        if (msg.matches(RegexConst.SHUTDOWN_BOT)) {
            if (Global.bot_adminId != userId) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("此操作仅管理员可执行").build(), false);
                return MESSAGE_IGNORE;
            }
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).text(Global.bot_botName + "服务停止中，正在释放资源，请耐心等待～").build(), false);
            BotApplication.shutdown();
        }
        return MESSAGE_IGNORE;
    }

}
