package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.common.utils.CommonUtils;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.shiro.bot.Bot;
import com.mikuac.shiro.bot.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.utils.Msg;
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
    public int onPrivateMessage(Bot bot, PrivateMessageEvent event) {
        long upTime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        if (msg.matches(RegexConst.PING)) {
            String imgUrl = commonUtils.getHostAndPort() + "/img/ping.jpg";
            bot.sendPrivateMsg(userId, Msg.builder().text("UpTime: " + upTime + "s\n").img(imgUrl).build(), false);
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(Bot bot, GroupMessageEvent event) {
        long upTime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        if (msg.matches(RegexConst.PING)) {
            String imgUrl = commonUtils.getHostAndPort() + "/img/ping.jpg";
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("UpTime: " + upTime + "s\n").img(imgUrl).build(), false);
        }
        return MESSAGE_IGNORE;
    }

}
