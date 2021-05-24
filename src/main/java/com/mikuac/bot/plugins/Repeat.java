package com.mikuac.bot.plugins;

import com.mikuac.bot.config.Global;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;
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

    Map<Long, String> lastMsgMap = new ConcurrentHashMap<>();
    Map<Long, Integer> countMap = new ConcurrentHashMap<>();

    /**
     * 产生一个min-max之间的随机数
     * result为2排除正常指令，并且max+1
     *
     * @return 返回一个int值
     */
    public int randomCount() {
        int max = Global.repeat_randomCountSize + 1;
        int min = 2;
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    int randomCount;

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        long groupId = event.getGroupId();

        String lastMsg = lastMsgMap.getOrDefault(groupId, "");
        int count = countMap.getOrDefault(groupId, 1);

        if (msg.equals(lastMsg)) {
            countMap.put(groupId, ++count);
            if (count == randomCount) {
                bot.sendGroupMsg(groupId, msg, false);
                log.info("复读成功，复读内容：[{}]", msg);
                countMap.put(groupId, 0);
                randomCount = randomCount();
            }
        } else {
            countMap.put(groupId, 1);
            lastMsgMap.put(groupId, msg);
            randomCount = randomCount();
        }
        return MESSAGE_IGNORE;
    }

}
