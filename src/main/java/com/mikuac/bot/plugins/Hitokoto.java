package com.mikuac.bot.plugins;

import com.alibaba.fastjson.JSONObject;
import com.mikuac.bot.utils.HttpClientUtil;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一言
 * @author Zero
 * @date 2020/11/3 9:34
 */
public class Hitokoto extends BotPlugin {

    /**
     * 初始化typesMap
     */
    Map<Character, String> typesMap = new ConcurrentHashMap() {
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

    @Value("${yuri.plugins.hitokoto-config.api}")
    private String api;
    @Value("${yuri.plugins.hitokoto-config.cdTime}")
    private int cdTime;
    @Value("${yuri.plugins.hitokoto-config.msgMatch}")
    private String msgMatch;

    private String types = "abcdefghijkl";

    private String hitokoto;
    private String from;
    private char getType;

    Map<Long, Long> lastGetTimeMap = new ConcurrentHashMap<>();

    public void getData(char type) {
        String result = HttpClientUtil.httpGetWithJson(api + type);
        JSONObject jsonObject = JSONObject.parseObject(result);
        hitokoto = (String) jsonObject.get("hitokoto");
        from = (String) jsonObject.get("from");
        getType = (char) jsonObject.get("h");
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        long getNowTime = event.getTime();
        long lastGetTime = lastGetTimeMap.getOrDefault(groupId + userId, 0L);
        // 消息处理
        if (msg.matches(msgMatch) && getNowTime >= lastGetTime + cdTime) {
            String msgType = msg.replaceAll("(.*?)-", "");
            if (msgType.matches("[a-l]")) {
                getData(msgType.charAt(0));
            } else {
                getData(types.charAt((int) (Math.random() * 12)));
            }
            String type = typesMap.get(getType);
            Msg msgBuilder = Msg.builder()
                    .at(userId)
                    .text("『" + hitokoto + "』\n" + "出自：" + from + "\n" + "类型：" + type);
            bot.sendGroupMsg(groupId, msgBuilder, false);
            lastGetTimeMap.put(groupId + userId, event.getTime());
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        long getNowTime = event.getTime();
        long lastGetTime = lastGetTimeMap.getOrDefault(userId, 0L);
        String msgType = msg.replaceAll("(.*?)-", "");
        // 消息处理
        if (msg.matches(msgMatch) && getNowTime >= lastGetTime + cdTime) {
            if (msgType.matches("[a-l]")) {
                getData(msgType.charAt(0));
            } else {
                getData(types.charAt((int) (Math.random() * 12)));
            }
            String type = typesMap.get(getType);
            Msg msgBuilder = Msg.builder()
                    .text("『" + hitokoto + "』\n" + "出自：" + from + "\n" + "类型：" + type);
            bot.sendPrivateMsg(userId, msgBuilder, false);
            lastGetTimeMap.put(userId, event.getTime());
        }
        return MESSAGE_IGNORE;
    }

}