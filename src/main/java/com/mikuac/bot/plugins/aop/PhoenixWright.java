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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 逆转裁判字体图片生成
 *
 * @author Zero
 * @date 2021/5/13 9:25
 */
@Component
public class PhoenixWright extends BotPlugin {

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String msg = event.getMessage();
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        if (msg.matches(RegexConst.PHOENIX_WRIGHT)) {
            String topText = URLEncoder.encode(Objects.requireNonNull(RegexUtils.regexGroup(RegexConst.PHOENIX_WRIGHT, msg, 1)), StandardCharsets.UTF_8);
            String bottomText = URLEncoder.encode(Objects.requireNonNull(RegexUtils.regexGroup(RegexConst.PHOENIX_WRIGHT, msg, 2)), StandardCharsets.UTF_8);
            MsgUtils sendMsg = MsgUtils.builder()
                    .at(userId)
                    .img("https://gsapi.cyberrex.ml/image?top=" + topText + "&bottom=" + bottomText);
            bot.sendGroupMsg(groupId, sendMsg.build(), false);
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, PrivateMessageEvent event) {
        String msg = event.getMessage();
        long userId = event.getUserId();
        if (msg.matches(RegexConst.PHOENIX_WRIGHT)) {
            String topText = URLEncoder.encode(Objects.requireNonNull(RegexUtils.regexGroup(RegexConst.PHOENIX_WRIGHT, msg, 1)), StandardCharsets.UTF_8);
            String bottomText = URLEncoder.encode(Objects.requireNonNull(RegexUtils.regexGroup(RegexConst.PHOENIX_WRIGHT, msg, 2)), StandardCharsets.UTF_8);
            MsgUtils sendMsg = MsgUtils.builder().img("https://gsapi.cyberrex.ml/image?top=" + topText + "&bottom=" + bottomText);
            bot.sendPrivateMsg(userId, sendMsg.build(), false);
        }
        return MESSAGE_IGNORE;
    }

}
