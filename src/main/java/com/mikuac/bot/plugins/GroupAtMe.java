package com.mikuac.bot.plugins;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.mikuac.bot.common.utils.CommonUtils;
import com.mikuac.bot.common.utils.RegexUtils;
import com.mikuac.bot.config.Global;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.shiro.bot.Bot;
import com.mikuac.shiro.bot.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.utils.Msg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Zero
 * @date 2020/12/7 13:19
 */
@Slf4j
@Component
public class GroupAtMe extends BotPlugin {

    TimedCache<Long, Boolean> timedCache = CacheUtil.newTimedCache(5000);

    @Resource
    private CommonUtils commonUtils;

    @Override
    public int onGroupMessage(Bot bot, GroupMessageEvent event) {
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        String msg = event.getRawMessage();
        // // 判断是否已经回复过
        if (timedCache.get(userId + groupId) != null) {
            return MESSAGE_IGNORE;
        }
        // 判断被at的是否为机器人
        String atId = RegexUtils.regexGroup(RegexConst.GROUP_AT, msg, 1);
        if (atId != null && !atId.isEmpty()) {
            long botId = Long.parseLong(atId);
            if (Global.bot_selfId == botId) {
                String imgUrl = commonUtils.getHostAndPort() + "/img/atme.jpg";
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).img(imgUrl).build(), false);
                timedCache.put(userId + groupId, true);
                log.info("@BOT 来自群组：[{}]的用户：[{}]", groupId, userId);
            }
        }
        return MESSAGE_IGNORE;
    }

}