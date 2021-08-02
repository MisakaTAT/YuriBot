package com.mikuac.bot.plugins;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.mikuac.bot.common.utils.CommonUtils;
import com.mikuac.bot.config.Global;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotBase;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Zero
 * @date 2020/12/7 13:19
 */
@Slf4j
@Component
public class GroupAtMe extends BotPlugin {

    TimedCache<Long, Boolean> timedCache = CacheUtil.newTimedCache(5000);

    private final static String MSG_TYPE = "at";

    private final static String AT_ALL = "all";

    @Resource
    private CommonUtils commonUtils;

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        // 判断是否已经回复过
        if (timedCache.get(userId + groupId) != null) {
            return MESSAGE_IGNORE;
        }
        // 判断是否为回复
        OnebotBase.Message reply = event.getMessageList().stream().filter(message -> "reply".equals(message.getType())).findFirst().orElse(null);
        if (reply != null) {
            return MESSAGE_IGNORE;
        }
        // 获取消息链
        List<OnebotBase.Message> messageChain = event.getMessageList();
        if (messageChain.size() > 0) {
            OnebotBase.Message message = messageChain.get(0);
            // 判断消息类型是否为at，否则return
            String msgType = message.getType();
            if (!MSG_TYPE.equals(msgType)) {
                return MESSAGE_IGNORE;
            }
            // 判断被at的是否为机器人
            String rowAtMsg = message.getDataMap().get("qq");
            // 排除全体at
            if (!AT_ALL.equals(rowAtMsg)) {
                long botId = Long.parseLong(rowAtMsg);
                if (Global.bot_selfId == botId) {
                    String imgUrl = commonUtils.getHostAndPort() + "/img/atme.jpg";
                    bot.sendGroupMsg(groupId, Msg.builder().at(userId).image(imgUrl).build(), false);
                    timedCache.put(userId + groupId, true);
                    log.info("@BOT 来自群组：[{}]的用户：[{}]", groupId, userId);
                }
            }

        }
        return MESSAGE_IGNORE;
    }

}