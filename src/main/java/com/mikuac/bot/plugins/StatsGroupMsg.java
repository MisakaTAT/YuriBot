package com.mikuac.bot.plugins;

import com.mikuac.bot.utils.SendMsgUtils;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 发言统计
 * @author Zero
 * @date 2020/11/16 19:03
 */
@Component
public class StatsGroupMsg extends BotPlugin {

    private SendMsgUtils sendMsgUtils;

    @Autowired
    public void setSendMsgUtils(SendMsgUtils sendMsgUtils) {
        this.sendMsgUtils = sendMsgUtils;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.initialize();
        return (TaskScheduler)taskScheduler;
    }

    Map<Long, Integer> totalMsg = new ConcurrentHashMap<>();

    @Scheduled(cron = "0 0 00 * * ?",zone = "Asia/Shanghai")
    public void sendMsg() {
        for (long groupId : sendMsgUtils.getGroupList()) {
            Msg msg = Msg.builder()
                    .text("龙王");
            sendMsgUtils.sendGroupMsg(groupId,msg);
        }
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        int nowMsgCount = totalMsg.getOrDefault(userId,0);
        if (nowMsgCount != 0) {
            totalMsg.put(userId, nowMsgCount + 1);
        }else {
            totalMsg.put(userId, 1);
        }
        return MESSAGE_IGNORE;
    }

}
