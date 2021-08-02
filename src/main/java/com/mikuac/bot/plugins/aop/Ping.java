package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.common.utils.CommonUtils;
import com.mikuac.bot.config.RegexConst;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
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
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        long upTime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        if (msg.matches(RegexConst.PING)) {
            String imgUrl = commonUtils.getHostAndPort() + "/img/ping.jpg";
            Msg.builder().text("UpTime: " + upTime + "s\n").image(imgUrl).sendToFriend(bot, userId);
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        long upTime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        if (msg.matches(RegexConst.PING)) {
            String imgUrl = commonUtils.getHostAndPort() + "/img/ping.jpg";
            Msg.builder().at(userId).text("UpTime: " + upTime + "s\n").image(imgUrl).sendToGroup(bot, groupId);
        }
        return MESSAGE_IGNORE;
    }

}
