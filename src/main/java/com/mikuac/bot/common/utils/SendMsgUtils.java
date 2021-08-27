package com.mikuac.bot.common.utils;

import com.mikuac.bot.config.Global;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * QQ主动消息推送工具类
 *
 * @author Zero
 * @date 2020/11/2 19:42
 */
@Slf4j
@Component
public class SendMsgUtils {

    @Resource
    private BotContainer botContainer;

    public void sendPrivateMsgForMsg(long userId, MsgUtils msg) {
        Bot bot = botContainer.robots.get(Global.BOT_SELF_ID);
        bot.sendPrivateMsg(userId, msg.build(), false);
    }

    public void sendGroupMsgForMsg(long groupId, MsgUtils msg) {
        Bot bot = botContainer.robots.get(Global.BOT_SELF_ID);
        bot.sendGroupMsg(groupId, msg.build(), false);
    }

    public void sendPrivateMsgForText(long userId, String msg) {
        Bot bot = botContainer.robots.get(Global.BOT_SELF_ID);
        bot.sendPrivateMsg(userId, msg, false);
    }

    public void sendGroupMsgForText(long groupId, String msg) {
        Bot bot = botContainer.robots.get(Global.BOT_SELF_ID);
        bot.sendGroupMsg(groupId, msg, false);
    }

    public List<Long> getGroupList() throws InterruptedException {
        int retryCount = 6;
        int retryDelay = 10000;

        List<Long> groupIdList = new ArrayList<>();

        //获取Bot对象
        Bot bot = botContainer.robots.get(Global.BOT_SELF_ID);
        if (bot == null) {
            for (int i = 1; i < retryCount; i++) {
                log.info("Bot对象获取失败，当前失败[{}]次，剩余重试次数[{}]，将在" + (retryDelay / 1000) + "秒后重试~", i, retryCount - i - 1);
                Thread.sleep(retryDelay);
                bot = botContainer.robots.get(Global.BOT_SELF_ID);
                if (bot != null) {
                    log.info("Bot对象获取成功[{}]", bot);
                    break;
                }
                if (i == 5) {
                    log.error("Bot对象获取失败5次，将中止此函数");
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
                    groupCount = bot.getGroupList().getData().size();
                }
                if (groupCount > 0) {
                    log.info("群组计数获取成功，当前群组数量[{}]", groupCount);
                    //遍历群号
                    for (int j = 0; j < groupCount; j++) {
                        groupIdList.add(bot.getGroupList().getData().get(j).getGroupId());
                    }
                    break;
                } else {
                    log.error("群组计数获取失败，且未发生异常，将中止此函数，当前群组计数[{}]", groupCount);
                    return groupIdList;
                }
            } catch (Exception e) {
                log.error("群组计数获取失败，当前失败[{}]次，剩余重试次数[{}]，将在" + (retryDelay / 1000) + "秒后重试~", i, retryCount - i - 1);
                if (i == 5) {
                    log.error("群组计数获取失败5次，将中止此函数");
                    return groupIdList;
                }
                Thread.sleep(retryDelay);
            }
        }

        return groupIdList;

    }

}