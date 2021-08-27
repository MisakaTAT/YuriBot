package com.mikuac.bot.plugins;

import cn.hutool.dfa.WordTree;
import com.mikuac.bot.config.Global;
import com.mikuac.bot.entity.SensitiveWordEntity;
import com.mikuac.bot.repository.SensitiveWordRepository;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

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
    WordTree wordTree = new WordTree();
    @Resource
    private SensitiveWordRepository sensitiveWordRepository;

    public void removeWord(String word) {
        wordTree.clear();
        init();
    }

    public void addWord(String word) {
        wordTree.addWord(word);
    }

    /**
     * 容器实例化Bean构造器,服务初始化
     */
    @PostConstruct
    public void init() {
        try {
            List<SensitiveWordEntity> wordList = sensitiveWordRepository.findAll();
            if (wordList.size() <= 0) {
                log.info("从数据库加载敏感词失败");
                return;
            }
            for (SensitiveWordEntity sensitiveWordEntity : wordList) {
                // 添加到前缀树
                wordTree.addWord(sensitiveWordEntity.getWord());
            }
            log.info("SensitiveWord 词库加载完成，当前词条数 [{}]", wordTree.size());
        } catch (Exception e) {
            log.info("SensitiveWord 词库加载异常: [{}]", e.getMessage());
        }
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String msg = event.getMessage().replaceAll("\\s*", "");
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        int msgId = event.getMessageId();
        // 检查Bot是否有管理员权限
        ActionData<GroupMemberInfoResp> groupBotInfo = bot.getGroupMemberInfo(groupId, Global.BOT_SELF_ID, false);
        if (groupBotInfo != null && groupBotInfo.getData() != null) {
            if (!ADMIN_ROLE.equals(groupBotInfo.getData().getRole())) {
                return MESSAGE_IGNORE;
            }
        }
        // 检查发送者是否为管理员或群主或者Bot管理员
        ActionData<GroupMemberInfoResp> groupMemberInfo = bot.getGroupMemberInfo(groupId, userId, false);
        if (groupMemberInfo != null && groupMemberInfo.getData() != null) {
            String getRole = groupMemberInfo.getData().getRole();
            if (ADMIN_ROLE.equals(getRole) || Global.BOT_ADMIN_ID == userId || OWNER_ROLE.equals(getRole)) {
                return MESSAGE_IGNORE;
            }
        }
        // 检查是否为敏感词
        if (wordTree.isMatch(msg)) {
            bot.deleteMsg(msgId);
            MsgUtils sendMsg = MsgUtils.builder()
                    .at(userId)
                    .text(Global.BOT_NAME + "注意到您发送到内容存在不适当的内容，已撤回处理，请注意言行哟～");
            bot.sendGroupMsg(groupId, sendMsg.build(), false);
            log.info("检测到敏感词: [{}]，来自群: [{}]，发送者: [{}]", msg, groupId, userId);
            // 如果是敏感词就阻止后面的插件执行，防止复读
            return MESSAGE_BLOCK;
        }
        return MESSAGE_IGNORE;
    }

}