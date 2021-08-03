package com.mikuac.bot.plugins;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.RandomUtil;
import com.mikuac.bot.config.Global;
import com.mikuac.shiro.bot.Bot;
import com.mikuac.shiro.bot.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 复读插件
 *
 * @author Zero
 * @date 2020/10/24 20:27
 */
@Slf4j
@Component
public class Repeat extends BotPlugin {

    /**
     * 创建缓存，1分钟内不复读同一内容
     */
    TimedCache<Long, String> timedCache = CacheUtil.newTimedCache(60 * 1000);

    /**
     * 最后一条消息
     */
    Map<Long, String> lastMsgMap = new ConcurrentHashMap<>();

    /**
     * 消息统计
     */
    Map<Long, Integer> countMap = new ConcurrentHashMap<>();

    int randomCount = RandomUtil.randomInt(Global.repeat_randomCountSize);

    @Override
    public int onGroupMessage(Bot bot, GroupMessageEvent event) {
        String msg = event.getRawMessage();
        long groupId = event.getGroupId();

        // 获取Map中当前群组最后一条消息内容
        String lastMsg = lastMsgMap.getOrDefault(groupId, "");
        // 获取当前群组同内容消息重复次数
        int count = countMap.getOrDefault(groupId, 0);

        // 过滤指令
        if (msg.startsWith(Global.prefix_prefix)) {
            return MESSAGE_IGNORE;
        }

        // 如果缓存中存在内容则不进行复读
        String cache = timedCache.get(groupId, false);
        if (cache != null && cache.equals(msg)) {
            return MESSAGE_IGNORE;
        }

        if (msg.equals(lastMsg)) {
            countMap.put(groupId, ++count);
            if (count == randomCount) {
                bot.sendGroupMsg(groupId, msg, false);
                timedCache.put(groupId, msg);
                countMap.put(groupId, 0);
                log.info("复读成功，复读内容：[{}]", msg);
            }
        } else {
            lastMsgMap.put(groupId, msg);
            countMap.put(groupId, 0);
        }

        return MESSAGE_IGNORE;
    }

}
