package com.mikuac.bot.plugins;

import com.mikuac.bot.config.RegexConst;
import com.mikuac.bot.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author Zero
 * @date 2020/12/7 13:19
 */
@Slf4j
@Component
public class GroupAtMe extends BotPlugin {

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        String botId = String.valueOf(event.getSelfId());
        String getAtId = RegexUtils.regex(RegexConst.AT_ME,msg);
        if (botId.equals(getAtId)) {
            String imgUrl = "https://mikuac.com/botimg/atme.jpg";
            long userId = event.getUserId();
            long groupId = event.getGroupId();
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).image(imgUrl).build(),false);
            log.info("@BOT 来自群组：[{}]的用户：[{}]",groupId,userId);
        }
        return MESSAGE_IGNORE;
    }

}