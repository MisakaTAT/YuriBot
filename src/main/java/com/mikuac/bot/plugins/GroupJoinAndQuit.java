package com.mikuac.bot.plugins;

import com.mikuac.bot.config.Global;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.notice.GroupDecreaseNoticeEvent;
import com.mikuac.shiro.dto.event.notice.GroupIncreaseNoticeEvent;
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
        if (userId == Global.botSelfId) {
            return MESSAGE_IGNORE;
        }
        MsgUtils msg = MsgUtils.builder()
                .at(userId)
                .text("Hi~ 我是" + Global.botBotName + "，欢迎加入本群，如果想了解我，请发送 " + Global.prefixPrefix + "帮助 或 " + Global.prefixPrefix + "help获取帮助信息~");
        bot.sendGroupMsg(groupId, msg.build(), false);
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupDecreaseNotice(@NotNull Bot bot, @NotNull GroupDecreaseNoticeEvent event) {
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        MsgUtils msg = MsgUtils.builder()
                .text(userId + "退出群聊");
        bot.sendGroupMsg(groupId, msg.build(), false);
        return MESSAGE_IGNORE;
    }

}