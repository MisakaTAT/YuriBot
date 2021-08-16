package com.mikuac.bot.plugins;

import com.mikuac.bot.common.utils.TrieUtils;
import com.mikuac.bot.config.Global;
import com.mikuac.shiro.bot.Bot;
import com.mikuac.shiro.bot.BotPlugin;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.utils.Msg;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 敏感词撤回
 *
 * @author Zero
 */
@Slf4j
@Component
public class SensitiveWords extends BotPlugin {

    private final static String ADMIN_ROLE = "admin";

    private final static String OWNER_ROLE = "owner";

    private TrieUtils trieUtils;

    @Autowired
    public void setTrieUtils(TrieUtils trieUtils) {
        this.trieUtils = trieUtils;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String msg = event.getMessage().replaceAll("\\s*", "");
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        int msgId = event.getMessageId();
        // 检查Bot是否有管理员权限
        ActionData<GroupMemberInfoResp> groupBotInfo = bot.getGroupMemberInfo(groupId, Global.botSelfId, false);
        if (groupBotInfo != null && groupBotInfo.getData() != null) {
            if (!ADMIN_ROLE.equals(groupBotInfo.getData().getRole())) {
                return MESSAGE_IGNORE;
            }
        }
        // 检查发送者是否为管理员或群主或者Bot管理员
        ActionData<GroupMemberInfoResp> groupMemberInfo = bot.getGroupMemberInfo(groupId, userId, false);
        if (groupMemberInfo != null && groupMemberInfo.getData() != null) {
            String getRole = groupMemberInfo.getData().getRole();
            if (ADMIN_ROLE.equals(getRole) || Global.botAdminId == userId || OWNER_ROLE.equals(getRole)) {
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
                    .text(Global.botBotName + "注意到您发送到内容存在不适当的内容，已撤回处理，请注意言行哟～");
            bot.sendGroupMsg(groupId, sendMsg.build(), false);
            log.info("检测到敏感词: [{}], 来自群: [{}], 发送者: [{}]", msg, groupId, userId);
        }
        return MESSAGE_IGNORE;
    }

}