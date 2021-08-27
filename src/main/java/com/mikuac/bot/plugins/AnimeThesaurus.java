package com.mikuac.bot.plugins;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.mikuac.bot.common.utils.FileUtils;
import com.mikuac.bot.common.utils.RegexUtils;
import com.mikuac.bot.config.Global;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created on 2021/8/13.
 *
 * @author Zero
 */
@Slf4j
@Component
public class AnimeThesaurus extends BotPlugin {

    private final static String IMAGE_CQ_CODE = "[CQ:image";
    JSONObject jsonObject;

    @PostConstruct
    private void init() {
        jsonObject = new JSONObject(FileUtils.readFile("anime_thesaurus.json"));
        log.info("AnimeThesaurus 词库加载完成，索引大小 [{}]", jsonObject.size());
    }

    private String getRespList(String msg) {
        for (Object key : jsonObject.keySet()) {
            if (msg.contains(key.toString())) {
                List<String> respWorldList = JSONArray.parseArray(jsonObject.get(key.toString()).toString(), String.class);
                return RandomUtil.randomEle(respWorldList);
            }
        }
        return null;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String msg = event.getMessage();
        long groupId = event.getGroupId();
        int msgId = event.getMessageId();
        if (ShiroUtils.isAtAll(msg) || msg.contains(IMAGE_CQ_CODE)) {
            return MESSAGE_IGNORE;
        }
        // 判断被at的是否为机器人
        String atId = RegexUtils.regexGroup(RegexConst.GROUP_AT, msg, 1);
        if (atId != null && !atId.isEmpty()) {
            long botId = Long.parseLong(atId);
            if (Global.BOT_SELF_ID == botId) {
                msg = msg.replace(String.format("[CQ:at,qq=%s] ", Global.BOT_SELF_ID), "");
                String sendMsg = getRespList(msg);
                if (sendMsg == null) {
                    return MESSAGE_IGNORE;
                }
                bot.sendGroupMsg(groupId, MsgUtils.builder().reply(msgId).text(sendMsg).build(), false);
            }
        }
        return MESSAGE_IGNORE;

    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull PrivateMessageEvent event) {
        String msg = event.getMessage();
        long userId = event.getUserId();
        String sendMsg = getRespList(msg);
        if (msg.contains(IMAGE_CQ_CODE) || sendMsg == null) {
            return MESSAGE_IGNORE;
        }
        bot.sendPrivateMsg(userId, sendMsg, false);
        return MESSAGE_IGNORE;
    }

}
