package com.mikuac.bot.plugins;

import com.alibaba.fastjson.JSON;
import com.mikuac.bot.bean.setu.Data;
import com.mikuac.bot.bean.setu.SetuBean;
import com.mikuac.bot.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotContainer;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zero
 * @date 2020/11/9 14:19
 */
@Slf4j
@Component
public class SeTu extends BotPlugin {

    private SetuBean seTuBean;

    @Autowired
    public void setSeTuBean(SetuBean seTuBean) {
        this.seTuBean = seTuBean;
    }

    private BotContainer botContainer;

    @Autowired
    public void setBotContainer(BotContainer botContainer) {
        this.botContainer = botContainer;
    }

    @Value("${yuri.bot.selfId}")
    private Long botId;
    @Value("${yuri.plugins.setu-config.api}")
    private String api;
    @Value("${yuri.plugins.setu-config.apiKey}")
    private String apiKey;
    @Value("${yuri.plugins.setu-config.cdTime}")
    private int cdTime;
    @Value("${yuri.plugins.setu-config.delTime}")
    private int delTime;
    @Value("${yuri.plugins.setu-config.maxGet}")
    private int maxGet;
    @Value("${yuri.plugins.setu-config.msgMatch}")
    private String msgMatch;

    private String picUrl;

    Map<Long, Long> lastGetTimeMap = new ConcurrentHashMap<>();

    Map<Long, Integer> getCountMap = new ConcurrentHashMap<>();

    @Scheduled(cron = "0 0 00 * * ?")
    public void clearMaxGetCount() {
        getCountMap.clear();
        log.info("色图每日上限已清除");
    }

    public void getData(String r18) {
        String result = HttpClientUtil.httpGetWithJson(api + apiKey + r18,false);
        seTuBean = JSON.parseObject(result, SetuBean.class);
    }

    public Msg getDataAndBuilder() {
        Msg msg = Msg.builder();
        for (Data data : seTuBean.getData()) {
            msg.text("标题：" + data.getTitle());
            msg.text("\nPID：" + data.getPid());
            msg.text("\n作者：" + data.getAuthor());
            msg.text("\n链接：" + "https://www.pixiv.net/artworks/" + data.getPid());
            msg.text("\n反代链接：" + data.getUrl());
            picUrl = data.getUrl();
        }
        return msg;
    }

    @Async
    public void deleteMsg(int msgId) {
        Bot bot = botContainer.getBots().get(botId);
        if (msgId != 0) {
            try {
                Thread.sleep(delTime*1000);
                bot.deleteMsg(msgId);
                log.info("色图撤回成功，消息ID：[{}]", msgId);
            } catch (InterruptedException e) {
                log.info("色图撤回异常：[{}]", e);
            }
        }
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        String msg = event.getRawMessage();
        // 私聊消息处理
        if (msg.matches(msgMatch)) {
            long userId = event.getUserId();
            long getNowTime = event.getTime()/1000;
            long lastGetTime = lastGetTimeMap.getOrDefault(userId, 0L)/1000;
            long rCd = Math.abs((getNowTime - lastGetTime)-cdTime);
            // 逻辑处理
            if (getNowTime >= lastGetTime + cdTime) {
                bot.sendPrivateMsg(userId, "少女祈祷中~",false);
                try {
                    getData(msg.matches("(.*?)[rR]18(.*)") ? "&r18=1" : "&r18=0");
                    lastGetTimeMap.put(userId, event.getTime());
                    bot.sendPrivateMsg(userId, getDataAndBuilder().build(),false);
                    int msgId = bot.sendPrivateMsg(userId,Msg.builder().image(picUrl).build(),false).getMessageId();
                    deleteMsg(msgId);
                } catch (Exception e) {
                    bot.sendPrivateMsg(userId, "图片获取失败，请稍后重试~", false);
                    log.info("色图私聊发送异常 [{}]", e);
                }
            } else {
                lastGetTimeMap.put(userId, 0L);
                bot.sendPrivateMsg(userId,"请求过于频繁~ 剩余CD时间为" + rCd + "秒",false);
            }
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        // 私聊消息处理
        if (msg.matches(msgMatch)) {
            long userId = event.getUserId();
            long groupId = event.getGroupId();
            long getNowTime = event.getTime()/1000;
            long lastGetTime = lastGetTimeMap.getOrDefault(userId+groupId, 0L)/1000;
            long rCd = Math.abs((getNowTime - lastGetTime)-cdTime);
            // 逻辑处理
            int count = getCountMap.get(userId) == null ? 0 : getCountMap.get(userId);
            if (getNowTime >= lastGetTime + cdTime && count < maxGet) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("少女祈祷中~").build(),false);
                try {
                    getData(msg.matches("(.*?)[rR]18(.*)") ? "&r18=1" : "&r18=0");
                    getCountMap.put(userId, count + 1);
                    lastGetTimeMap.put(userId+groupId, event.getTime());
                    bot.sendGroupMsg(groupId, getDataAndBuilder().at(userId).build(),false);
                    int msgId = bot.sendGroupMsg(groupId,Msg.builder().image(picUrl).build(),false).getMessageId();
                    deleteMsg(msgId);
                } catch (Exception e) {
                    getCountMap.put(userId, getCountMap.get(userId)-1);
                    lastGetTimeMap.put(userId + groupId, 0L);
                    bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("图片获取失败，请稍后重试~").build(), false);
                    log.info("色图私聊发送异常 [{}]", e);
                }
            } else if (count == maxGet) {
                bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("今日获取次数已达上限，每晚24点重置~").build(),false);
            } else {
                bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("请求过于频繁~ 剩余CD时间为" + rCd + "秒").build(),false);
            }
        }
        return MESSAGE_IGNORE;
    }

}