package com.mikuac.bot.plugins;

import com.mikuac.bot.common.utils.TrieUtils;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotApi;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    private TrieUtils trieUtils;

    @Autowired
    public void setTrieUtils(TrieUtils trieUtils) {
        this.trieUtils = trieUtils;
    }

    private final static String ADMIN_ROLE = "admin";

    private final static String OWNER_ROLE = "owner";

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage().replaceAll("\\s*", "");
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        int msgId = event.getMessageId();
        // 检查Bot是否有管理员权限
        OnebotApi.GetGroupMemberInfoResp groupBotInfo = bot.getGroupMemberInfo(groupId, selfId, false);
        if (groupBotInfo != null) {
            if (!ADMIN_ROLE.equals(groupBotInfo.getRole())) {
                return MESSAGE_IGNORE;
            }
        }
        // 检查发送者是否为管理员或群主或者Bot管理员
        OnebotApi.GetGroupMemberInfoResp groupMemberInfo = bot.getGroupMemberInfo(groupId, userId, false);
        if (groupMemberInfo != null) {
            String getRole = groupMemberInfo.getRole();
            if (ADMIN_ROLE.equals(getRole) || adminId == userId || OWNER_ROLE.equals(getRole)) {
                return MESSAGE_IGNORE;
            }
        }
        // 检查是否为敏感词
        if (trieUtils.contains(msg)) {
            if (msgId <= 0) {
                return MESSAGE_IGNORE;
            }
            // 如果获取到了消息ID则撤回消息
            bot.deleteMsg(msgId);
            Msg sendMsg = Msg.builder()
                    .at(userId)
                    .text(botName + "注意到您发送到内容存在不适当的内容，已撤回处理，请注意言行哟～");
            bot.sendGroupMsg(groupId, sendMsg.build(), false);
            log.info("检测到敏感词: [{}], 来自群: [{}], 发送者: [{}]", msg, groupId, userId);
        }
        return MESSAGE_IGNORE;
    }

}