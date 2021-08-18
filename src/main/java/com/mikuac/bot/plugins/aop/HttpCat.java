package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.common.utils.RegexUtils;
import com.mikuac.bot.config.ApiConst;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.shiro.bot.Bot;
import com.mikuac.shiro.bot.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.utils.Msg;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Created on 2021/8/18.
 *
 * @author Zero
 */
@Component
public class HttpCat extends BotPlugin {

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String msg = event.getMessage();
        if (msg.matches(RegexConst.HTTP_CAT)) {
            String httpCode = RegexUtils.regexGroup(RegexConst.HTTP_CAT, msg, 1);
            if (httpCode != null && !httpCode.isEmpty()) {
                Msg sendMsg = Msg.builder().reply(event.getMessageId()).img(ApiConst.HTTP_CAT + httpCode);
                bot.sendGroupMsg(event.getGroupId(), sendMsg.build(), false);
            }
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull PrivateMessageEvent event) {
        String msg = event.getMessage();
        if (msg.matches(RegexConst.HTTP_CAT)) {
            String httpCode = RegexUtils.regexGroup(RegexConst.HTTP_CAT, msg, 1);
            if (httpCode != null && !httpCode.isEmpty()) {
                Msg sendMsg = Msg.builder().img(ApiConst.HTTP_CAT + httpCode);
                bot.sendPrivateMsg(event.getUserId(), sendMsg.build(), false);
            }
        }
        return MESSAGE_IGNORE;
    }

}
