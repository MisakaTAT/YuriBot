package com.mikuac.bot.plugins;

import com.mikuac.bot.config.Global;
import com.mikuac.shiro.bot.Bot;
import com.mikuac.shiro.bot.BotPlugin;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import com.mikuac.shiro.dto.event.notice.PokeNoticeEvent;
import com.mikuac.shiro.utils.Msg;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Created on 2021/8/16.
 *
 * @author Zero
 */
@Slf4j
@Component
public class Poke extends BotPlugin {

    @Override
    public int onGroupPokeNotice(@NotNull Bot bot, @NotNull PokeNoticeEvent event) {
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        long targetId = event.getTargetId();

        if (Global.botSelfId == targetId) {
            bot.sendGroupMsg(groupId, Msg.builder().poke(userId).build(), false);
        }

        GroupMemberInfoResp userInfo = bot.getGroupMemberInfo(groupId, userId, true).getData();
        GroupMemberInfoResp targetInfo = bot.getGroupMemberInfo(groupId, targetId, true).getData();
        if (userInfo != null && targetInfo != null) {
            log.info("Poke事件: 群[{}]，用户[{}]，目标[{}]", groupId, userInfo.getNickname(), targetInfo.getNickname());
            return MESSAGE_IGNORE;
        }

        log.info("Poke事件: 群[{}]，用户[{}]，目标[{}]", groupId, userId, targetId);
        return MESSAGE_IGNORE;
    }

}
