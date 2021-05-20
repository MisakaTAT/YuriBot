package com.mikuac.bot.plugins;

import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotApi;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 敏感词撤回
 *
 * @author Zero
 */
@Slf4j
@Component
public class SensitiveWords extends BotPlugin {

    @Value("${yuri.bot.botName}")
    private String botName;

    @Value("${yuri.bot.selfId}")
    private long selfId;

    @Value("${yuri.bot.adminId}")
    private long adminId;

    List<String> wordList = new ArrayList<>();

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        long startTime = System.currentTimeMillis();
        String msg = event.getRawMessage().replaceAll("\s*", "");
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        int msgId = event.getMessageId();

        // 检查是否有管理员权限
        OnebotApi.GetGroupMemberInfoResp groupMemberInfo = bot.getGroupMemberInfo(groupId, selfId, false);
        if (groupMemberInfo != null) {
            String role = "admin";
            if (!role.equals(groupMemberInfo.getRole()) || adminId == userId) {
                return MESSAGE_BLOCK;
            }
        }

        for (String word : wordList) {
            // 检查是否为敏感词
            if (msg.matches("^(?i)(.*)" + word + "(.*)")) {
                if (msgId <= 0) {
                    return MESSAGE_BLOCK;
                }
                // 如果获取到了消息ID则撤回消息
                bot.deleteMsg(msgId);
                Msg sendMsg = Msg.builder()
                        .at(userId)
                        .text(botName + "注意到您发送到内容存在不适当的内容，已撤回处理，请注意言行哟～");
                bot.sendGroupMsg(groupId, sendMsg.build(), false);
                log.info("检测到敏感词: [{}], 来自群: [{}], 发送者: [{}]", msg, groupId, userId);
                break;
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        return MESSAGE_IGNORE;
    }

}
