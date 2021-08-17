package com.mikuac.bot.plugins.aop;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mikuac.bot.common.utils.HttpClientUtils;
import com.mikuac.bot.config.ApiConst;
import com.mikuac.bot.config.Global;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.shiro.bot.Bot;
import com.mikuac.shiro.bot.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.utils.Msg;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一言
 *
 * @author Zero
 * @date 2020/11/3 9:34
 */
@Slf4j
@Component
public class Hitokoto extends BotPlugin {

    private final String types = "abcdefghijkl";

    /**
     * 初始化typesMap
     */
    Map<Character, String> typesMap = new ConcurrentHashMap<>() {
        {
            put('a', "动画");
            put('b', "漫画");
            put('c', "游戏");
            put('d', "文学");
            put('e', "原创");
            put('f', "来自网络");
            put('g', "其他");
            put('h', "影视");
            put('i', "诗词");
            put('j', "网易云");
            put('k', "哲学");
            put('l', "抖机灵");
        }
    };

    Map<Long, Long> lastGetTimeMap = new ConcurrentHashMap<>();

    @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    private String hitokoto;

    @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    private String from;

    @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    private char getType;

    public void getData(char type) {
        String result = HttpClientUtils.httpGetWithJson(ApiConst.HITOKOTO_API + type, false);
        JSONObject jsonObject = JSONObject.parseObject(result);
        hitokoto = jsonObject.getString("hitokoto");
        from = jsonObject.getString("from");
        getType = jsonObject.getString("type").charAt(0);
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String msg = event.getMessage();
        // 群组消息处理
        if (msg.matches(RegexConst.HITOKOTO)) {
            long groupId = event.getGroupId();
            long userId = event.getUserId();
            long getNowTime = Instant.now().getEpochSecond();
            long lastGetTime = lastGetTimeMap.getOrDefault(groupId + userId, 0L);
            long rCd = Math.abs((getNowTime - lastGetTime) - Global.hitokotoCdTime);
            // 逻辑处理
            if (getNowTime >= lastGetTime + Global.hitokotoCdTime) {
                try {
                    bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("一言获取中~").build(), false);
                    String msgType = msg.replaceAll("(.*?)-", "");
                    if (msgType.matches("[a-l]")) {
                        getData(msgType.charAt(0));
                    } else {
                        getData(types.charAt((int) (Math.random() * 12)));
                    }
                    String type = typesMap.get(getType);
                    Msg msgBuilder = Msg.builder()
                            .reply(event.getMessageId())
                            .text("『" + hitokoto + "』\n" + "出自：" + from + "\n" + "类型：" + type);
                    bot.sendGroupMsg(groupId, msgBuilder.build(), false);
                    lastGetTimeMap.put(groupId + userId, Instant.now().getEpochSecond());
                } catch (Exception e) {
                    bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("一言获取失败,请稍后重试~").build(), false);
                    log.error("一言群组发送异常: {}", e.getMessage());
                }
            } else {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("请求过于频繁~ 剩余CD时间为" + rCd + "秒").build(), false);
            }
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, PrivateMessageEvent event) {
        String msg = event.getMessage();
        // 私聊消息处理
        if (msg.matches(RegexConst.HITOKOTO)) {
            long userId = event.getUserId();
            long getNowTime = Instant.now().getEpochSecond();
            long lastGetTime = lastGetTimeMap.getOrDefault(userId, 0L);
            long rCd = Math.abs((getNowTime - lastGetTime) - Global.hitokotoCdTime);
            // 逻辑处理
            if (getNowTime >= lastGetTime + Global.hitokotoCdTime) {
                bot.sendPrivateMsg(userId, "一言获取中~", false);
                String msgType = msg.replaceAll("(.*?)-", "");
                try {
                    if (msgType.matches("[a-l]")) {
                        getData(msgType.charAt(0));
                    } else {
                        getData(types.charAt((int) (Math.random() * 12)));
                    }
                    String type = typesMap.get(getType);
                    Msg msgBuilder = Msg.builder()
                            .text("『" + hitokoto + "』\n" + "出自：" + from + "\n" + "类型：" + type);
                    bot.sendPrivateMsg(userId, msgBuilder.build(), false);
                    lastGetTimeMap.put(userId, Instant.now().getEpochSecond());
                } catch (Exception e) {
                    bot.sendPrivateMsg(userId, "一言获取失败,请稍后重试~", false);
                    log.error("一言私聊发送异常: {}", e.getMessage());
                }
            } else {
                bot.sendPrivateMsg(userId, "请求过于频繁~ 剩余CD时间为" + rCd + "秒", false);
            }
        }
        return MESSAGE_IGNORE;
    }

}
