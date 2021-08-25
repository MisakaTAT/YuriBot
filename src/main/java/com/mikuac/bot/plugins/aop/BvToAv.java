package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.common.utils.RegexUtils;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 算法实现av bv互转
 *
 * @author Zero
 * @date 2020/12/9 11:46
 */
@Component
public class BvToAv extends BotPlugin {

    // 算法来源
    // https://www.zhihu.com/question/381784377/answer/1099438784

    String table = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF";

    int[] s = {11, 10, 3, 8, 4, 6};
    long xor = 177451812L;
    long add = 8728348608L;

    Map<String, Integer> bv2avMap = new ConcurrentHashMap<>();
    Map<Integer, String> av2bvMap = new ConcurrentHashMap<>();

    public String bv2av(String bvId) {
        long r = 0;
        for (int i = 0; i < 58; i++) {
            bv2avMap.put(String.valueOf(table.charAt(i)), i);
        }
        for (int i = 0; i < 6; i++) {
            r += bv2avMap.get(bvId.substring(s[i], s[i] + 1)) * Math.pow(58, i);
        }
        return "av" + ((r - add) ^ xor);
    }

    public String av2bv(String avId) {
        long aid = Long.parseLong(avId.split("av")[1]);
        StringBuilder stringBuilder = new StringBuilder("BV1  4 1 7  ");
        aid = (aid ^ xor) + add;
        for (int i = 0; i < 58; i++) {
            av2bvMap.put(i, String.valueOf(table.charAt(i)));
        }
        for (int i = 0; i < 6; i++) {
            String r = av2bvMap.get((int) (aid / Math.pow(58, i) % 58));
            stringBuilder.replace(s[i], s[i] + 1, r);
        }
        return stringBuilder.toString();
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, PrivateMessageEvent event) {
        String msg = event.getMessage();
        long userId = event.getUserId();
        if (msg.matches(RegexConst.AV_TO_BV)) {
            String avId = RegexUtils.regex(RegexConst.AV_TO_BV_GET_ID, msg);
            if (avId != null) {
                bot.sendPrivateMsg(userId, av2bv(avId), false);
            } else {
                bot.sendPrivateMsg(userId, "未获取到AV号，请检查后重新尝试~", false);
            }
        }
        if (msg.matches(RegexConst.BV_TO_AV)) {
            String bvId = RegexUtils.regex(RegexConst.AV_TO_BV_GET_ID, msg);
            if (bvId != null) {
                bot.sendPrivateMsg(userId, bv2av(bvId), false);
            } else {
                bot.sendPrivateMsg(userId, "未获取到BV号，请检查后重新尝试~", false);
            }
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String msg = event.getMessage();
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        if (msg.matches(RegexConst.AV_TO_BV)) {
            String avId = RegexUtils.regex(RegexConst.AV_TO_BV_GET_ID, msg);
            if (avId != null) {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text(av2bv(avId)).build(), false);
            } else {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("未获取到AV号，请检查后重新尝试~").build(), false);
            }
        }
        if (msg.matches(RegexConst.BV_TO_AV)) {
            String bvId = RegexUtils.regex(RegexConst.AV_TO_BV_GET_ID, msg);
            if (bvId != null) {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text(bv2av(bvId)).build(), false);
            } else {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("未获取到BV号，请检查后重新尝试~").build(), false);
            }
        }
        return MESSAGE_IGNORE;
    }

}
