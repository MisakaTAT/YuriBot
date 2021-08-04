package com.mikuac.bot.plugins;

import com.mikuac.bot.config.Global;
import com.mikuac.shiro.bot.Bot;
import com.mikuac.shiro.bot.BotPlugin;
import com.mikuac.shiro.dto.event.notice.GroupDecreaseNoticeEvent;
import com.mikuac.shiro.dto.event.notice.GroupIncreaseNoticeEvent;
import com.mikuac.shiro.utils.Msg;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 入群欢迎 & 退群提醒
 *
 * @author Zero
 * @date 2020/11/4 14:48
 */
@Slf4j
@Component
public class GroupJoinAndQuit extends BotPlugin {

    @Override
    public int onGroupIncreaseNotice(@NotNull Bot bot, @NotNull GroupIncreaseNoticeEvent event) {
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        // 排除BOT自身入群通知
        if (userId == Global.bot_selfId) {
            return MESSAGE_IGNORE;
        }
        Msg msg = Msg.builder()
                .at(userId)
                .text("Hi~ 我是" + Global.bot_botName + "，欢迎加入本群，如果想了解我，请发送 " + Global.prefix_prefix + "帮助 或 " + Global.prefix_prefix + "help获取帮助信息~");
        bot.sendGroupMsg(groupId, msg.build(), false);
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupDecreaseNotice(@NotNull Bot bot, @NotNull GroupDecreaseNoticeEvent event) {
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        Msg msg = Msg.builder()
                .text(userId + "退出群聊");
        bot.sendGroupMsg(groupId, msg.build(), false);
        return MESSAGE_IGNORE;
    }

}