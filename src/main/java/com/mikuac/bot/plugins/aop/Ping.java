package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.common.utils.CommonUtils;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.management.ManagementFactory;

/**
 * Created on 2021/8/2.
 *
 * @author Zero
 */
@Component
public class Ping extends BotPlugin {

    @Resource
    private CommonUtils commonUtils;

    @Override
    public int onPrivateMessage(@NotNull Bot bot, PrivateMessageEvent event) {
        long upTime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        String msg = event.getMessage();
        long userId = event.getUserId();
        if (msg.matches(RegexConst.PING)) {
            String imgUrl = commonUtils.getHostAndPort() + "/img/ping.jpg";
            bot.sendPrivateMsg(userId, MsgUtils.builder().text("运行时长: " + CommonUtils.secondFormat(upTime) + "\n").img(imgUrl).build(), false);
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        long upTime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        String msg = event.getMessage();
        long groupId = event.getGroupId();
        int msgId = event.getMessageId();
        if (msg.matches(RegexConst.PING)) {
            String imgUrl = commonUtils.getHostAndPort() + "/img/ping.jpg";
            bot.sendGroupMsg(groupId, MsgUtils.builder().reply(msgId).text("运行时长: " + CommonUtils.secondFormat(upTime) + "\n").img(imgUrl).build(), false);
        }
        return MESSAGE_IGNORE;
    }

}
