package com.mikuac.bot.plugins.aop;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 消息监听日志
 *
 * @author Zero
 * @date 2020/10/23 22:50
 */
@Slf4j
@Component
public class EventLog extends BotPlugin {

    @Override
    public int onPrivateMessage(@NotNull Bot bot, PrivateMessageEvent event) {
        log.info("收到私聊消息 QQ：[{}] 内容：[{}]", event.getUserId(), event.getMessage());
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        log.info("收到群消息 群号：[{}] QQ：[{}] 内容：[{}]", event.getGroupId(), event.getUserId(), event.getMessage());
        return MESSAGE_IGNORE;
    }

}
