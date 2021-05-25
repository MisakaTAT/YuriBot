package com.mikuac.bot.common.utils;

import com.mikuac.bot.config.Global;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotContainer;
import net.lz1998.pbbot.utils.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * QQ主动消息推送工具类
 *
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

    /**
     * 私聊消息发送
     *
     * @param userId 用户ID
     * @param msg    消息
     */
    @Async("taskExecutor")
    public void sendPrivateMsg(long userId, Msg msg) throws InterruptedException {
        Bot bot = botContainer.getBots().get(Global.bot_selfId);
        // 限制发送速度
        Thread.sleep(1000);
        bot.sendPrivateMsg(userId, msg.build(), false);
    }

    /**
     * 群组消息发送
     *
     * @param groupId 群组ID
     * @param msg     消息
     */
    @Async("taskExecutor")
    public void sendGroupMsg(long groupId, Msg msg) throws InterruptedException {
        Bot bot = botContainer.getBots().get(Global.bot_selfId);
        // 限制发送速度
        Thread.sleep(1000);
        bot.sendGroupMsg(groupId, msg.build(), false);
    }

    /**
     * 获取群号列表
     *
     * @return
     * @throws InterruptedException
     */
    public List<Long> getGroupList() throws InterruptedException {
        int retryCount = 6;
        int retryDelay = 10000;

        List<Long> groupIdList = new ArrayList<>();

        //获取Bot对象
        Bot bot = botContainer.getBots().get(Global.bot_selfId);
        if (bot == null) {
            for (int i = 1; i < retryCount; i++) {
                log.info("Bot对象获取失败，当前失败[{}]次，剩余重试次数[{}]，将在" + (retryDelay / 1000) + "秒后重试~", i, retryCount - i - 1);
                Thread.sleep(retryDelay);
                bot = botContainer.getBots().get(Global.bot_selfId);
                if (bot != null) {
                    log.info("Bot对象获取成功[{}]", bot);
                    break;
                }
                if (i == 5) {
                    log.info("Bot对象获取失败5次，将中止此函数");
                    return groupIdList;
                }
            }
        } else {
            log.info("Bot对象获取成功[{}]", bot);
        }

        //获取群号列表
        for (int i = 1; i < retryCount; i++) {
            try {
                int groupCount = 0;
                if (bot != null) {
                    groupCount = Objects.requireNonNull(bot.getGroupList()).getGroupCount();
                }
                if (groupCount > 0) {
                    log.info("群组计数获取成功，当前群组数量[{}]", groupCount);
                    //遍历群号
                    for (int j = 0; j < groupCount; j++) {
                        groupIdList.add(bot.getGroupList().getGroup(j).getGroupId());
                    }
                    break;
                } else {
                    log.info("群组计数获取失败，且未发生异常，将中止此函数，当前群组计数[{}]", groupCount);
                    return groupIdList;
                }
            } catch (Exception e) {
                log.info("群组计数获取失败，当前失败[{}]次，剩余重试次数[{}]，将在" + (retryDelay / 1000) + "秒后重试~", i, retryCount - i - 1);
                if (i == 5) {
                    log.info("群组计数获取失败5次，将中止此函数");
                    return groupIdList;
                }
                Thread.sleep(retryDelay);
            }
        }

        return groupIdList;

    }

}