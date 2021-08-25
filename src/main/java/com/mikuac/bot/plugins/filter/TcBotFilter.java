package com.mikuac.bot.plugins.filter;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 过滤腾讯官方机器人
 *
 * @author Zero
 * @date 2020/12/14 11:07
 */
@Slf4j
@Component
public class TcBotFilter extends BotPlugin {

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        long userId = event.getUserId();
        long startId = 2854196300L;
        long endId = 2854216399L;
        if (userId >= startId && userId <= endId) {
            log.info("已拦截QQ官方机器人消息，ID：[{}]", userId);
            return MESSAGE_BLOCK;
        }
        return MESSAGE_IGNORE;
    }

}
