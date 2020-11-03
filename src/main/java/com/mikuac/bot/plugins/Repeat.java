package com.mikuac.bot.plugins;

import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 复读插件
 * @author Zero
 * @date 2020/10/24 20:27
 */
@Slf4j
@Component
public class Repeat extends BotPlugin {

    Map<Long, String> lastMsgMap = new ConcurrentHashMap<>();
    Map<Long, Integer> countMap = new ConcurrentHashMap<>();

    @Value("${yuri.plugins.repeat-config.randomCountSize}")
    private int randomCountSize;

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        Random random = new Random();
        int randomCount = random.nextInt(randomCountSize);
        String msg = event.getRawMessage();
        long groupId = event.getGroupId();

        String lastMsg = lastMsgMap.getOrDefault(groupId, "");
        Integer count = countMap.getOrDefault(groupId, 0);

        if (msg.equals(lastMsg)) {
            count++;
            countMap.put(groupId, count);
            if (count == randomCount + 1) {
                bot.sendGroupMsg(groupId, msg, false);
                log.info("复读成功，复读内容：[{}]", msg);
                countMap.put(groupId, 0);
            }
        } else {
            countMap.put(groupId, 0);
            lastMsgMap.put(groupId, msg);
        }
        return MESSAGE_IGNORE;
    }

}
