package com.mikuac.bot.plugins;

import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Objects;

/**
 * 入群欢迎 & 退群提醒
 * @author Zero
 * @date 2020/11/4 14:48
 */
@Slf4j
@Component
public class GroupJoinAndQuit extends BotPlugin {

    @Value("${yuri.bot.botName}")
    private String botName;

    @Override
    public int onGroupIncreaseNotice(@NotNull Bot bot, @NotNull OnebotEvent.GroupIncreaseNoticeEvent event) {
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        Msg msg = Msg.builder()
                .at(userId)
                .text("Hi~ 我是"+botName+"，欢迎加入本群，如果想了解我，请发送 #帮助 或 #help获取帮助信息~");
        bot.sendGroupMsg(groupId,msg.build(),false);
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupDecreaseNotice(@NotNull Bot bot, @NotNull OnebotEvent.GroupDecreaseNoticeEvent event) {
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        String nickName = "昵称获取失败";
        try {
            nickName = Objects.requireNonNull(bot.getGroupMemberInfo(groupId, userId, true)).getNickname();
            log.info("退群检测插件用户名获取成功：[{}] QQ：[{}]",nickName,userId);
        } catch (NullPointerException e) {
            log.info("退群检测插件用户名获取失败",e);
        }
        Msg msg = Msg.builder()
                .text(nickName + "(" + userId + ")" + "由于精神失常退出了此群");
        bot.sendGroupMsg(groupId,msg.build(),false);
        return MESSAGE_IGNORE;
    }

}