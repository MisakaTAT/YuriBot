package com.mikuac.bot.utils;

import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotContainer;
import net.lz1998.pbbot.utils.Msg;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * QQ主动消息推送工具类
 * @author Zero
 * @date 2020/11/2 19:42
 */
@Slf4j
@Component
public class SendMsgUtils {

    private BotContainer botContainer;

    @Autowired
    public void setBotContainer(BotContainer botContainer) {
        this.botContainer = botContainer;
    }

    @Value("${yuri.bot.selfId}")
    private Long botId;

    /**
     * 私聊消息发送
     * @param userId
     * @param msg
     */
    public void sendPrivateMsg(long userId, Msg msg) throws InterruptedException {
        Bot bot = botContainer.getBots().get(botId);
        // 限制发送速度
        Thread.sleep(1000);
        bot.sendPrivateMsg(userId, msg.build(), false);
    }

    /**
     * 群组消息发送
     * @param groupId
     * @param msg
     */
    public void sendGroupMsg(long groupId, Msg msg) throws InterruptedException {
        Bot bot = botContainer.getBots().get(botId);
        // 限制发送速度
        Thread.sleep(1000);
        bot.sendGroupMsg(groupId, msg.build(), false);
    }

    /**
     * 获取群组列表，返回List
     * @return
     */
    public List<Long> getGroupList() throws InterruptedException {

        int retryCount = 6;

        List<Long> groupIdList = new ArrayList<>();

        //获取Bot对象
        Bot bot = botContainer.getBots().get(botId);
        if (bot == null) {
            for (int i = 1; i < retryCount ;i++) {
                log.info("Bot对象获取失败，当前失败[{}]次，剩余重试次数[{}]，将在10秒后重试~",i,retryCount-i-1);
                Thread.sleep(10000);
                bot = botContainer.getBots().get(botId);
                if (bot != null) {
                    log.info("Bot对象获取成功[{}]",bot);
                    break;
                }
            }
            log.info("Bot对象获取失败5次，将中止此函数并返回null值");
            return null;
        } else {
            log.info("Bot对象获取成功[{}]",bot);
        }

        //获取群组列表数
        try {
            int groupCount = Objects.requireNonNull(bot.getGroupList()).getGroupCount();
            log.info("群组计数获取成功，当前群组数量[{}]",groupCount);
            //遍历群号
            for (int i = 0; i < groupCount; i++) {
                groupIdList.add(bot.getGroupList().getGroup(i).getGroupId());
            }
        } catch (NullPointerException e) {
            log.info("群组计数获取失败",e);
        }

        return groupIdList;

    }

}