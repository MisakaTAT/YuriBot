package com.mikuac.bot.common.utils;

import com.mikuac.bot.config.Config;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.dto.action.response.GroupInfoResp;
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
        Bot bot = botContainer.robots.get(Config.BOT_SELF_ID);
        bot.sendPrivateMsg(userId, msg.build(), false);
    }

    public void sendGroupMsgForMsg(long groupId, MsgUtils msg) {
        Bot bot = botContainer.robots.get(Config.BOT_SELF_ID);
        bot.sendGroupMsg(groupId, msg.build(), false);
    }

    public void sendPrivateMsgForText(long userId, String msg) {
        Bot bot = botContainer.robots.get(Config.BOT_SELF_ID);
        bot.sendPrivateMsg(userId, msg, false);
    }

    public void sendGroupMsgForText(long groupId, String msg) {
        Bot bot = botContainer.robots.get(Config.BOT_SELF_ID);
        bot.sendGroupMsg(groupId, msg, false);
    }

    public List<Long> getGroupList() throws InterruptedException {
        List<Long> groupIdList = new ArrayList<>();
        Bot bot = botContainer.robots.get(Config.BOT_SELF_ID);
        if (bot != null) {
            for (GroupInfoResp groupInfoResp : bot.getGroupList().getData()) {
                groupIdList.add(groupInfoResp.getGroupId());
            }
        }
        return groupIdList;
    }

}