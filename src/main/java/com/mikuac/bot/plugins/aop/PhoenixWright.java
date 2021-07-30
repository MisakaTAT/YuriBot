package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.common.utils.RegexUtils;
import com.mikuac.bot.config.RegexConst;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
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
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        if (msg.matches(RegexConst.PHOENIX_WRIGHT)) {
            String topText = URLEncoder.encode(Objects.requireNonNull(RegexUtils.regexGroup(RegexConst.PHOENIX_WRIGHT, msg, 1)), StandardCharsets.UTF_8);
            String bottomText = URLEncoder.encode(Objects.requireNonNull(RegexUtils.regexGroup(RegexConst.PHOENIX_WRIGHT, msg, 2)), StandardCharsets.UTF_8);
            Msg sendMsg = Msg.builder()
                    .at(userId)
                    .image("https://gsapi.cyberrex.ml/image?top=" + topText + "&bottom=" + bottomText);
            bot.sendGroupMsg(groupId, sendMsg.build(), false);
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        if (msg.matches(RegexConst.PHOENIX_WRIGHT)) {
            String topText = URLEncoder.encode(Objects.requireNonNull(RegexUtils.regexGroup(RegexConst.PHOENIX_WRIGHT, msg, 1)), StandardCharsets.UTF_8);
            String bottomText = URLEncoder.encode(Objects.requireNonNull(RegexUtils.regexGroup(RegexConst.PHOENIX_WRIGHT, msg, 2)), StandardCharsets.UTF_8);
            Msg sendMsg = Msg.builder().image("https://gsapi.cyberrex.ml/image?top=" + topText + "&bottom=" + bottomText);
            bot.sendPrivateMsg(userId, sendMsg.build(), false);
        }
        return MESSAGE_IGNORE;
    }

}
