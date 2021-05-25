package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.common.task.AsyncTask;
import com.mikuac.bot.config.Global;
import com.mikuac.bot.config.RegexConst;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created on 2021/5/25.
 *
 * @author Zero
 */
@Slf4j
@Component
public class RebootBot extends BotPlugin {

    private AsyncTask asyncTask;

    @Autowired
    public void setAsyncTask(AsyncTask asyncTask) {
        this.asyncTask = asyncTask;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        if (msg.matches(RegexConst.REBOOT_BOT)) {
            if (Global.bot_adminId != userId) {
                bot.sendPrivateMsg(userId, "此操作仅管理员可执行", false);
                return MESSAGE_IGNORE;
            }
            bot.sendPrivateMsg(userId, Global.bot_botName + "即将进行重启，请耐心等待～", false);
            asyncTask.rebootBot();
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        if (msg.matches(RegexConst.REBOOT_BOT)) {
            if (Global.bot_adminId != userId) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("此操作仅管理员可执行").build(), false);
                return MESSAGE_IGNORE;
            }
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).text(Global.bot_botName + "即将进行重启，请耐心等待～").build(), false);
            asyncTask.rebootBot();
        }
        return MESSAGE_IGNORE;
    }

}
