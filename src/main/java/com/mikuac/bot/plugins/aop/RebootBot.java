package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.common.task.AsyncTask;
import com.mikuac.bot.config.Config;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import lombok.extern.slf4j.Slf4j;
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
    public int onPrivateMessage(@NotNull Bot bot, PrivateMessageEvent event) {
        String msg = event.getMessage();
        long userId = event.getUserId();
        if (msg.matches(RegexConst.REBOOT_BOT)) {
            if (Config.BOT_ADMIN_ID != userId) {
                bot.sendPrivateMsg(userId, "此操作仅管理员可执行", false);
                return MESSAGE_IGNORE;
            }
            bot.sendPrivateMsg(userId, Config.BOT_NAME + "即将进行重启，请耐心等待～", false);
            asyncTask.rebootBot();
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String msg = event.getMessage();
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        if (msg.matches(RegexConst.REBOOT_BOT)) {
            if (Config.BOT_ADMIN_ID != userId) {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("此操作仅管理员可执行").build(), false);
                return MESSAGE_IGNORE;
            }
            bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text(Config.BOT_NAME + "即将进行重启，请耐心等待～").build(), false);
            asyncTask.rebootBot();
        }
        return MESSAGE_IGNORE;
    }

}
