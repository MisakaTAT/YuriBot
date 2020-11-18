package com.mikuac.bot.utils;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotContainer;
import net.lz1998.pbbot.utils.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * QQ主动消息推送工具类
 * @author Zero
 * @date 2020/11/2 19:42
 */
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
    public List<Long> getGroupList() {
        Bot bot = botContainer.getBots().get(botId);
        List<Long> groupId = new ArrayList();
        int groupCount = Objects.requireNonNull(bot.getGroupList()).getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            groupId.add(bot.getGroupList().getGroup(i).getGroupId());
        }
        return groupId;
    }

}
